package com.hightechif.swipecleaner.domain.usecase

import com.hightechif.swipecleaner.data.repository.KeptPhotosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResetKeptPhotosUseCase(
    private val keptPhotosRepository: KeptPhotosRepository
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        keptPhotosRepository.clearAllKeptPhotos()
    }
}
