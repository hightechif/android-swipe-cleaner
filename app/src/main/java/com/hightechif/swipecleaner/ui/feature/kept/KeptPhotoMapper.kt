package com.hightechif.swipecleaner.ui.feature.kept

import com.hightechif.swipecleaner.domain.model.KeptPhoto
import com.hightechif.swipecleaner.domain.model.MediaImage

private const val UNKNOWN_BUCKET_ID = "unknown"
private const val UNKNOWN_BUCKET_NAME = "Others"

fun List<KeptPhoto>.toResolved(mediaImages: List<MediaImage>): List<ResolvedKeptPhoto> {
    val mediaByUri = mediaImages.associateBy { it.uri }
    return map { kept ->
        val media = mediaByUri[kept.uri]
        ResolvedKeptPhoto(
            uri = kept.uri,
            keptAt = kept.keptAt,
            bucketId = media?.bucketId ?: UNKNOWN_BUCKET_ID,
            bucketName = media?.bucketName ?: UNKNOWN_BUCKET_NAME
        )
    }
}

fun List<ResolvedKeptPhoto>.toKeptAlbums(): List<KeptAlbum> =
    groupBy { it.bucketId }
        .map { (bucketId, albumPhotos) ->
            val cover = albumPhotos.first()
            KeptAlbum(
                id = bucketId,
                name = cover.bucketName,
                coverPhotoUri = cover.uri,
                photoCount = albumPhotos.size,
                photos = albumPhotos
            )
        }
        .sortedBy { it.name }
