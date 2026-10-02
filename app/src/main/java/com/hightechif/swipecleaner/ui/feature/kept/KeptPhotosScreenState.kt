package com.hightechif.swipecleaner.ui.feature.kept

import com.hightechif.swipecleaner.domain.model.KeptPhoto
import com.hightechif.swipecleaner.domain.model.MediaImage

data class KeptPhotosScreenState(
    val keptPhotos: List<KeptPhoto> = emptyList(),
    val mediaImages: List<MediaImage> = emptyList()
)
