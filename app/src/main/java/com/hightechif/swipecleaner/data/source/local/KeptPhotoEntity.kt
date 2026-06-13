package com.hightechif.swipecleaner.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hightechif.swipecleaner.domain.model.KeptPhoto

@Entity(tableName = "kept_photos")
data class KeptPhotoEntity(
    @PrimaryKey
    val uri: String,
    val keptAt: Long
) {
    fun toDomain() = KeptPhoto(uri = uri, keptAt = keptAt)
}
