package com.hightechif.swipecleaner.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kept_photos")
data class KeptPhotoEntity(
    @PrimaryKey
    val uri: String,
    val keptAt: Long
)
