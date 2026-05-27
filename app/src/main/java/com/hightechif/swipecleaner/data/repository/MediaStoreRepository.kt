package com.hightechif.swipecleaner.data.repository

import android.content.Context
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri

data class Album(
    val id: String,
    val name: String,
    val coverPhotoUri: String,
    val photoCount: Int
)

data class MediaImage(
    val uri: String,
    val bucketId: String,
    val bucketName: String
)

interface MediaStoreRepository {
    fun queryAllImageUris(): List<String>
    fun queryImageUrisFromBucket(bucketId: String): List<String>
    fun queryAllAlbums(): List<Album>
    fun queryAllMediaImages(): List<MediaImage>
    fun createTrashRequest(uris: List<String>): IntentSender?
    fun deleteUrisLegacy(uris: List<String>): Boolean
}

class MediaStoreRepositoryImpl(
    private val context: Context
) : MediaStoreRepository {

    override fun queryAllImageUris(): List<String> {
        val uriList = mutableListOf<String>()
        val contentResolver = context.contentResolver

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Images.Media._ID
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        try {
            contentResolver.query(
                collection,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri = Uri.withAppendedPath(collection, id.toString())
                    uriList.add(contentUri.toString())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return uriList
    }

    override fun queryImageUrisFromBucket(bucketId: String): List<String> {
        val uriList = mutableListOf<String>()
        val contentResolver = context.contentResolver

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Images.Media._ID
        )

        val selection = "${MediaStore.Images.Media.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(bucketId)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        try {
            contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri = Uri.withAppendedPath(collection, id.toString())
                    uriList.add(contentUri.toString())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return uriList
    }

    override fun queryAllAlbums(): List<Album> {
        val albumsMap = mutableMapOf<String, AlbumBuilder>()
        val contentResolver = context.contentResolver

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        try {
            contentResolver.query(
                collection,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
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
            e.printStackTrace()
        }

        return albumsMap.values.map { it.build() }.sortedBy { it.name }
    }

    override fun queryAllMediaImages(): List<MediaImage> {
        val imagesList = mutableListOf<MediaImage>()
        val contentResolver = context.contentResolver

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        try {
            contentResolver.query(
                collection,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
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
            e.printStackTrace()
        }

        return imagesList
    }

    override fun createTrashRequest(uris: List<String>): IntentSender? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val contentResolver = context.contentResolver
            val uriList = uris.map { it.toUri() }
            return try {
                val pendingIntent = MediaStore.createTrashRequest(contentResolver, uriList, true)
                pendingIntent.intentSender
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        return null
    }

    override fun deleteUrisLegacy(uris: List<String>): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            val contentResolver = context.contentResolver
            var success = true
            for (uriStr in uris) {
                try {
                    val count = contentResolver.delete(uriStr.toUri(), null, null)
                    if (count <= 0) success = false
                } catch (e: Exception) {
                    e.printStackTrace()
                    success = false
                }
            }
            return success
        }
        return false
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
