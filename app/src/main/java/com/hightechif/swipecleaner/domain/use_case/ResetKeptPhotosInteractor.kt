package com.hightechif.swipecleaner.domain.use_case

import com.hightechif.swipecleaner.domain.repository.IKeptPhotosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResetKeptPhotosInteractor(
    private val keptPhotosRepository: IKeptPhotosRepository
) : ResetKeptPhotosUseCase {

    override suspend operator fun invoke() = withContext(Dispatchers.IO) {
        keptPhotosRepository.clearAllKeptPhotos()
    }
}
