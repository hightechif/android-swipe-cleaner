package com.hightechif.swipecleaner.data.repository

import android.content.Context
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import com.hightechif.swipecleaner.domain.model.Album
import com.hightechif.swipecleaner.domain.model.MediaImage
import com.hightechif.swipecleaner.domain.repository.IMediaStoreRepository
import timber.log.Timber

class MediaStoreRepository(
    private val context: Context
) : IMediaStoreRepository {

    override fun queryAllImageUris(): List<String> {
        val uriList = mutableListOf<String>()
        val collection = mediaCollection()

        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        try {
            context.contentResolver.query(collection, projection, null, null, sortOrder)?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    uriList.add(Uri.withAppendedPath(collection, id.toString()).toString())
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to query all image URIs")
        }

        return uriList
    }

    override fun queryImageUrisFromBucket(bucketId: String): List<String> {
        val uriList = mutableListOf<String>()
        val collection = mediaCollection()

        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(bucketId)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        try {
            context.contentResolver.query(collection, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    uriList.add(Uri.withAppendedPath(collection, id.toString()).toString())
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to query image URIs from bucket $bucketId")
        }

        return uriList
    }

    override fun queryAllAlbums(): List<Album> {
        val albumsMap = mutableMapOf<String, AlbumBuilder>()
        val collection = mediaCollection()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        try {
            context.contentResolver.query(collection, projection, null, null, sortOrder)?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
                val bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val bucketId = cursor.getString(bucketIdColumn) ?: continue
                    val bucketName = cursor.getString(bucketNameColumn) ?: "Unknown"
                    val uri = Uri.withAppendedPath(collection, id.toString()).toString()

                    val builder = albumsMap.getOrPut(bucketId) {
                        AlbumBuilder(id = bucketId, name = bucketName, coverPhotoUri = uri)
                    }
                    builder.count++
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to query all albums")
        }

        return albumsMap.values.map { it.build() }.sortedBy { it.name }
    }

    override fun queryAllMediaImages(): List<MediaImage> {
        val imagesList = mutableListOf<MediaImage>()
        val collection = mediaCollection()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        try {
            context.contentResolver.query(collection, projection, null, null, sortOrder)?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
                val bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val bucketId = cursor.getString(bucketIdColumn) ?: continue
                    val bucketName = cursor.getString(bucketNameColumn) ?: "Unknown"
                    val uri = Uri.withAppendedPath(collection, id.toString()).toString()
                    imagesList.add(MediaImage(uri, bucketId, bucketName))
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to query all media images")
        }

        return imagesList
    }

    override fun createTrashRequest(uris: List<String>): IntentSender? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return try {
                val pendingIntent = MediaStore.createTrashRequest(
                    context.contentResolver,
                    uris.map { it.toUri() },
                    true
                )
                pendingIntent.intentSender
            } catch (e: Exception) {
                Timber.e(e, "Failed to create trash request")
                null
            }
        }
        return null
    }

    override fun deleteUrisLegacy(uris: List<String>): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            var success = true
            for (uriStr in uris) {
                try {
                    val count = context.contentResolver.delete(uriStr.toUri(), null, null)
                    if (count <= 0) success = false
                } catch (e: Exception) {
                    Timber.e(e, "Failed to delete URI: $uriStr")
                    success = false
                }
            }
            return success
        }
        return false
    }

    private fun mediaCollection(): Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }
}

private class AlbumBuilder(
    val id: String,
    val name: String,
    val coverPhotoUri: String,
    var count: Int = 0
) {
    fun build() = Album(id, name, coverPhotoUri, count)
}
