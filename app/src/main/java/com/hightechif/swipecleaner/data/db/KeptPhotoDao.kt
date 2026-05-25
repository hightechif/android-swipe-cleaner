package com.hightechif.swipecleaner.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface KeptPhotoDao {
    @Query("SELECT * FROM kept_photos ORDER BY keptAt DESC")
    fun getAllKeptPhotosFlow(): Flow<List<KeptPhotoEntity>>

    @Query("SELECT * FROM kept_photos")
    suspend fun getAllKeptPhotos(): List<KeptPhotoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeptPhoto(keptPhoto: KeptPhotoEntity)

    @Query("DELETE FROM kept_photos")
    suspend fun deleteAllKeptPhotos()
}
