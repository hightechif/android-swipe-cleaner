package com.hightechif.swipecleaner.data.repository

import com.hightechif.swipecleaner.data.source.local.KeptPhotoDao
import com.hightechif.swipecleaner.data.source.local.KeptPhotoEntity
import com.hightechif.swipecleaner.domain.model.KeptPhoto
import com.hightechif.swipecleaner.domain.repository.IKeptPhotosRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class KeptPhotosRepository(
    private val keptPhotoDao: KeptPhotoDao
) : IKeptPhotosRepository {

    override fun getKeptPhotosFlow(): Flow<List<KeptPhoto>> =
        keptPhotoDao.getAllKeptPhotosFlow().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getKeptPhotos(): List<KeptPhoto> =
        keptPhotoDao.getAllKeptPhotos().map { it.toDomain() }

    override suspend fun insertKeptPhoto(uri: String) {
        keptPhotoDao.insertKeptPhoto(KeptPhotoEntity(uri = uri, keptAt = System.currentTimeMillis()))
    }

    override suspend fun clearAllKeptPhotos() {
        keptPhotoDao.deleteAllKeptPhotos()
    }

    override suspend fun deleteKeptPhoto(uri: String) {
        keptPhotoDao.deleteKeptPhoto(uri)
    }
}
