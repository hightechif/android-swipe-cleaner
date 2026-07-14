package com.hightechif.swipecleaner.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hightechif.swipecleaner.domain.model.TrashedPhoto

@Entity(tableName = "trashed_photos")
data class TrashedPhotoEntity(
    @PrimaryKey
    val uri: String,
    val trashedAt: Long
) {
    fun toDomain() = TrashedPhoto(uri = uri, trashedAt = trashedAt)
}
