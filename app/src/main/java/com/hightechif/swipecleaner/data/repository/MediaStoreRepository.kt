package com.hightechif.swipecleaner.data.repository

import android.content.Context
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri

interface MediaStoreRepository {
    fun queryAllImageUris(): List<String>
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
