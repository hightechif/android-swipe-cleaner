package com.hightechif.swipecleaner.domain.use_case

import com.hightechif.swipecleaner.domain.repository.ITrashedPhotosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddToTrashInteractor(
    private val trashedPhotosRepository: ITrashedPhotosRepository
) : AddToTrashUseCase {

    override suspend operator fun invoke(uri: String) = withContext(Dispatchers.IO) {
        trashedPhotosRepository.insertTrashedPhoto(uri)
    }
}
