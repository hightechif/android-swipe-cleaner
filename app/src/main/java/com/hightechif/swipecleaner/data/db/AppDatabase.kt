package com.hightechif.swipecleaner.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [KeptPhotoEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun keptPhotoDao(): KeptPhotoDao
}
