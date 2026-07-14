package com.hightechif.swipecleaner.domain.use_case

import com.hightechif.swipecleaner.domain.repository.ITrashedPhotosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClearTrashedPhotosInteractor(
    private val trashedPhotosRepository: ITrashedPhotosRepository
) : ClearTrashedPhotosUseCase {

    override suspend operator fun invoke() = withContext(Dispatchers.IO) {
        trashedPhotosRepository.clearAllTrashedPhotos()
    }
}
