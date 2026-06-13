package com.hightechif.swipecleaner.data.repository

import com.hightechif.swipecleaner.data.source.local.TrashedPhotoDao
import com.hightechif.swipecleaner.data.source.local.TrashedPhotoEntity
import com.hightechif.swipecleaner.domain.model.TrashedPhoto
import com.hightechif.swipecleaner.domain.repository.ITrashedPhotosRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrashedPhotosRepository(
    private val trashedPhotoDao: TrashedPhotoDao
) : ITrashedPhotosRepository {

    override fun getTrashedPhotosFlow(): Flow<List<TrashedPhoto>> =
        trashedPhotoDao.getAllTrashedPhotosFlow().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getTrashedPhotos(): List<TrashedPhoto> =
        trashedPhotoDao.getAllTrashedPhotos().map { it.toDomain() }

    override suspend fun insertTrashedPhoto(uri: String) {
        trashedPhotoDao.insertTrashedPhoto(TrashedPhotoEntity(uri = uri, trashedAt = System.currentTimeMillis()))
    }

    override suspend fun clearAllTrashedPhotos() {
        trashedPhotoDao.deleteAllTrashedPhotos()
    }

    override suspend fun deleteTrashedPhoto(uri: String) {
        trashedPhotoDao.deleteTrashedPhoto(uri)
    }
}
