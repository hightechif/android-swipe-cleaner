package com.hightechif.swipecleaner.data.repository

import com.hightechif.swipecleaner.data.db.KeptPhotoDao
import com.hightechif.swipecleaner.data.db.KeptPhotoEntity
import kotlinx.coroutines.flow.Flow

interface KeptPhotosRepository {
    fun getKeptPhotosFlow(): Flow<List<KeptPhotoEntity>>
    suspend fun getKeptPhotos(): List<KeptPhotoEntity>
    suspend fun insertKeptPhoto(uri: String)
    suspend fun clearAllKeptPhotos()
}

class KeptPhotosRepositoryImpl(
    private val keptPhotoDao: KeptPhotoDao
) : KeptPhotosRepository {

    override fun getKeptPhotosFlow(): Flow<List<KeptPhotoEntity>> {
        return keptPhotoDao.getAllKeptPhotosFlow()
    }

    override suspend fun getKeptPhotos(): List<KeptPhotoEntity> {
        return keptPhotoDao.getAllKeptPhotos()
    }

    override suspend fun insertKeptPhoto(uri: String) {
        val entity = KeptPhotoEntity(uri = uri, keptAt = System.currentTimeMillis())
        keptPhotoDao.insertKeptPhoto(entity)
    }

    override suspend fun clearAllKeptPhotos() {
        keptPhotoDao.deleteAllKeptPhotos()
    }
}
