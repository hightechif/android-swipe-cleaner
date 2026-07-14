package com.hightechif.swipecleaner.domain.use_case

import com.hightechif.swipecleaner.domain.model.KeptPhoto
import com.hightechif.swipecleaner.domain.repository.IKeptPhotosRepository
import kotlinx.coroutines.flow.Flow

class GetKeptPhotosInteractor(
    private val keptPhotosRepository: IKeptPhotosRepository
) : GetKeptPhotosUseCase {

    override operator fun invoke(): Flow<List<KeptPhoto>> = keptPhotosRepository.getKeptPhotosFlow()
}
