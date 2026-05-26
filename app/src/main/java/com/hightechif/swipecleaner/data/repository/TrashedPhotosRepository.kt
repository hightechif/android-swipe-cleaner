package com.hightechif.swipecleaner.data.repository

import com.hightechif.swipecleaner.data.db.TrashedPhotoDao
import com.hightechif.swipecleaner.data.db.TrashedPhotoEntity
import kotlinx.coroutines.flow.Flow

interface TrashedPhotosRepository {
    fun getTrashedPhotosFlow(): Flow<List<TrashedPhotoEntity>>
    suspend fun getTrashedPhotos(): List<TrashedPhotoEntity>
    suspend fun insertTrashedPhoto(uri: String)
    suspend fun clearAllTrashedPhotos()
    suspend fun deleteTrashedPhoto(uri: String)
}

class TrashedPhotosRepositoryImpl(
    private val trashedPhotoDao: TrashedPhotoDao
) : TrashedPhotosRepository {

    override fun getTrashedPhotosFlow(): Flow<List<TrashedPhotoEntity>> {
        return trashedPhotoDao.getAllTrashedPhotosFlow()
    }

    override suspend fun getTrashedPhotos(): List<TrashedPhotoEntity> {
        return trashedPhotoDao.getAllTrashedPhotos()
    }

    override suspend fun insertTrashedPhoto(uri: String) {
        val entity = TrashedPhotoEntity(uri = uri, trashedAt = System.currentTimeMillis())
        trashedPhotoDao.insertTrashedPhoto(entity)
    }

    override suspend fun clearAllTrashedPhotos() {
        trashedPhotoDao.deleteAllTrashedPhotos()
    }

    override suspend fun deleteTrashedPhoto(uri: String) {
        trashedPhotoDao.deleteTrashedPhoto(uri)
    }
}
