package com.hightechif.swipecleaner.domain.repository

import com.hightechif.swipecleaner.domain.model.KeptPhoto
import kotlinx.coroutines.flow.Flow

interface IKeptPhotosRepository {
    fun getKeptPhotosFlow(): Flow<List<KeptPhoto>>
    suspend fun getKeptPhotos(): List<KeptPhoto>
    suspend fun insertKeptPhoto(uri: String)
    suspend fun clearAllKeptPhotos()
    suspend fun deleteKeptPhoto(uri: String)
}
