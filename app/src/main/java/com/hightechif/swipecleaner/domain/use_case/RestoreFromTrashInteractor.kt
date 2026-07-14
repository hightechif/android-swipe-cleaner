package com.hightechif.swipecleaner.domain.use_case

import com.hightechif.swipecleaner.domain.repository.ITrashedPhotosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RestoreFromTrashInteractor(
    private val trashedPhotosRepository: ITrashedPhotosRepository
) : RestoreFromTrashUseCase {

    override suspend operator fun invoke(uri: String) = withContext(Dispatchers.IO) {
        trashedPhotosRepository.deleteTrashedPhoto(uri)
    }
}
