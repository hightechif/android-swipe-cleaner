package com.hightechif.swipecleaner.domain.repository

import com.hightechif.swipecleaner.domain.model.Album
import com.hightechif.swipecleaner.domain.model.MediaImage
import com.hightechif.swipecleaner.domain.model.PendingSystemAction

interface IMediaStoreRepository {
    fun queryAllImageUris(): List<String>
    fun queryImageUrisFromBucket(bucketId: String): List<String>
    fun queryAllAlbums(): List<Album>
    fun queryAllMediaImages(): List<MediaImage>
    fun createTrashRequest(uris: List<String>): PendingSystemAction
    fun deleteUrisLegacy(uris: List<String>): Boolean
}
