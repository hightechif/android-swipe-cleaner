package com.hightechif.swipecleaner.data.repository

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import com.hightechif.swipecleaner.domain.model.Album
import com.hightechif.swipecleaner.domain.model.MediaImage
import com.hightechif.swipecleaner.domain.model.PendingSystemAction
import com.hightechif.swipecleaner.domain.repository.IMediaStoreRepository
import timber.log.Timber

private const val UNKNOWN_ALBUM_NAME = "Unknown"

class MediaStoreRepository(
    private val context: Context
) : IMediaStoreRepository {

    override fun queryAllImageUris(): List<String> =
        queryImages(errorMessage = "Failed to query all image URIs") { _, _, uri -> uri }

    override fun queryImageUrisFromBucket(bucketId: String): List<String> =
        queryImages(
            selection = "${MediaStore.Images.Media.BUCKET_ID} = ?",
            selectionArgs = arrayOf(bucketId),
            errorMessage = "Failed to query image URIs from bucket $bucketId"
        ) { _, _, uri -> uri }

    override fun queryAllAlbums(): List<Album> =
        queryAllMediaImages()
            .groupBy { it.bucketId }
            .map { (bucketId, images) ->
                val cover = images.first()
                Album(
                    id = bucketId,
                    name = cover.bucketName,
                    coverPhotoUri = cover.uri,
                    photoCount = images.size
                )
            }
            .sortedBy { it.name }

    override fun queryAllMediaImages(): List<MediaImage> =
        queryImages(errorMessage = "Failed to query all media images") { bucketId, bucketName, uri ->
            bucketId?.let { MediaImage(uri, it, bucketName) }
        }

    private fun <T : Any> queryImages(
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        errorMessage: String,
        transform: (bucketId: String?, bucketName: String, uri: String) -> T?
    ): List<T> {
        val results = mutableListOf<T>()
        val collection = mediaCollection()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        try {
            context.contentResolver.query(collection, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
                val bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val bucketId = cursor.getString(bucketIdColumn)
                    val bucketName = cursor.getString(bucketNameColumn) ?: UNKNOWN_ALBUM_NAME
                    val uri = Uri.withAppendedPath(collection, id.toString()).toString()
                    transform(bucketId, bucketName, uri)?.let(results::add)
                }
            }
        } catch (e: Exception) {
            Timber.e(e, errorMessage)
        }

        return results
    }

    override fun createTrashRequest(uris: List<String>): PendingSystemAction {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return try {
                val pendingIntent = MediaStore.createTrashRequest(
                    context.contentResolver,
                    uris.map { it.toUri() },
                    true
                )
                PendingSystemAction(pendingIntent.intentSender)
            } catch (e: Exception) {
                Timber.e(e, "Failed to create trash request")
                PendingSystemAction(null)
            }
        }
        return PendingSystemAction(null)
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
