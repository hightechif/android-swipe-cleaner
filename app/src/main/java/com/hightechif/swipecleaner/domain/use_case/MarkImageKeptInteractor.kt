package com.hightechif.swipecleaner.domain.use_case

import com.hightechif.swipecleaner.domain.repository.IKeptPhotosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MarkImageKeptInteractor(
    private val keptPhotosRepository: IKeptPhotosRepository
) : MarkImageKeptUseCase {

    override suspend operator fun invoke(uri: String) = withContext(Dispatchers.IO) {
        keptPhotosRepository.insertKeptPhoto(uri)
    }
}
