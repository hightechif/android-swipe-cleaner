package com.hightechif.swipecleaner.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trashed_photos")
data class TrashedPhotoEntity(
    @PrimaryKey
    val uri: String,
    val trashedAt: Long
)
