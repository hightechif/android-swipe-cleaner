package com.hightechif.swipecleaner.domain.repository

import com.hightechif.swipecleaner.domain.model.TrashedPhoto
import kotlinx.coroutines.flow.Flow

interface ITrashedPhotosRepository {
    fun getTrashedPhotosFlow(): Flow<List<TrashedPhoto>>
    suspend fun getTrashedPhotos(): List<TrashedPhoto>
    suspend fun insertTrashedPhoto(uri: String)
    suspend fun clearAllTrashedPhotos()
    suspend fun deleteTrashedPhoto(uri: String)
}
