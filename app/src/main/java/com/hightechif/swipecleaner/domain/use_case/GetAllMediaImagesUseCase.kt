package com.hightechif.swipecleaner.domain.use_case

import com.hightechif.swipecleaner.domain.model.MediaImage

interface GetAllMediaImagesUseCase {
    suspend operator fun invoke(): List<MediaImage>
}
