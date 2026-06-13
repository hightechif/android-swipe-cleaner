package com.hightechif.swipecleaner.ui.feature.kept

data class ResolvedKeptPhoto(
    val uri: String,
    val keptAt: Long,
    val bucketId: String,
    val bucketName: String
)

data class KeptAlbum(
    val id: String,
    val name: String,
    val coverPhotoUri: String,
    val photoCount: Int,
    val photos: List<ResolvedKeptPhoto>
)
