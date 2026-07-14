package com.hightechif.swipecleaner.domain.use_case

import com.hightechif.swipecleaner.domain.model.MediaImage
import com.hightechif.swipecleaner.domain.repository.IMediaStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetAllMediaImagesInteractor(
    private val mediaStoreRepository: IMediaStoreRepository
) : GetAllMediaImagesUseCase {

    override suspend operator fun invoke(): List<MediaImage> = withContext(Dispatchers.IO) {
        mediaStoreRepository.queryAllMediaImages()
    }
}
