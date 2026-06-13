package com.hightechif.swipecleaner.domain.use_case

import com.hightechif.swipecleaner.domain.model.TrashedPhoto
import com.hightechif.swipecleaner.domain.repository.ITrashedPhotosRepository
import kotlinx.coroutines.flow.Flow

class GetTrashedPhotosInteractor(
    private val trashedPhotosRepository: ITrashedPhotosRepository
) : GetTrashedPhotosUseCase {

    override operator fun invoke(): Flow<List<TrashedPhoto>> = trashedPhotosRepository.getTrashedPhotosFlow()
}
