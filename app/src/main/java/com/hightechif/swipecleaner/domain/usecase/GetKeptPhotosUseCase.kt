package com.hightechif.swipecleaner.domain.usecase

import com.hightechif.swipecleaner.data.db.KeptPhotoEntity
import com.hightechif.swipecleaner.data.repository.KeptPhotosRepository
import kotlinx.coroutines.flow.Flow

class GetKeptPhotosUseCase(
    private val keptPhotosRepository: KeptPhotosRepository
) {
    operator fun invoke(): Flow<List<KeptPhotoEntity>> {
        return keptPhotosRepository.getKeptPhotosFlow()
    }
}
