package com.hightechif.swipecleaner.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrashedPhotoDao {
    @Query("SELECT * FROM trashed_photos ORDER BY trashedAt DESC")
    fun getAllTrashedPhotosFlow(): Flow<List<TrashedPhotoEntity>>

    @Query("SELECT * FROM trashed_photos")
    suspend fun getAllTrashedPhotos(): List<TrashedPhotoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrashedPhoto(trashedPhoto: TrashedPhotoEntity)

    @Query("DELETE FROM trashed_photos")
    suspend fun deleteAllTrashedPhotos()

    @Query("DELETE FROM trashed_photos WHERE uri = :uri")
    suspend fun deleteTrashedPhoto(uri: String)
}
