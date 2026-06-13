package com.hightechif.swipecleaner.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [KeptPhotoEntity::class, TrashedPhotoEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun keptPhotoDao(): KeptPhotoDao
    abstract fun trashedPhotoDao(): TrashedPhotoDao
}
