package com.hightechif.swipecleaner.domain.use_case

import com.hightechif.swipecleaner.domain.model.KeptPhoto
import kotlinx.coroutines.flow.Flow

interface GetKeptPhotosUseCase {
    operator fun invoke(): Flow<List<KeptPhoto>>
}
