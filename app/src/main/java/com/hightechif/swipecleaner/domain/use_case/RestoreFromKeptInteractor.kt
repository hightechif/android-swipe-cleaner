package com.hightechif.swipecleaner.domain.use_case

import com.hightechif.swipecleaner.domain.repository.IKeptPhotosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RestoreFromKeptInteractor(
    private val keptPhotosRepository: IKeptPhotosRepository
) : RestoreFromKeptUseCase {

    override suspend operator fun invoke(uri: String) = withContext(Dispatchers.IO) {
        keptPhotosRepository.deleteKeptPhoto(uri)
    }
}
