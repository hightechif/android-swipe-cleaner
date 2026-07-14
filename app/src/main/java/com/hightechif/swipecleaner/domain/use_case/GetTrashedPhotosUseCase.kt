package com.hightechif.swipecleaner.domain.use_case

import com.hightechif.swipecleaner.domain.model.TrashedPhoto
import kotlinx.coroutines.flow.Flow

interface GetTrashedPhotosUseCase {
    operator fun invoke(): Flow<List<TrashedPhoto>>
}
