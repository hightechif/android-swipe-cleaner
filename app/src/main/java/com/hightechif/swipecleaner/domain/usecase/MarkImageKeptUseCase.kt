package com.hightechif.swipecleaner.domain.usecase

import com.hightechif.swipecleaner.data.repository.KeptPhotosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MarkImageKeptUseCase(
    private val keptPhotosRepository: KeptPhotosRepository
) {
    suspend operator fun invoke(uri: String) = withContext(Dispatchers.IO) {
        keptPhotosRepository.insertKeptPhoto(uri)
    }
}
