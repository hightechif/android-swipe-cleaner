package com.hightechif.swipecleaner.domain.use_case

import com.hightechif.swipecleaner.domain.model.PendingSystemAction
import com.hightechif.swipecleaner.domain.repository.IMediaStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExecuteTrashRequestInteractor(
    private val mediaStoreRepository: IMediaStoreRepository
) : ExecuteTrashRequestUseCase {

    override suspend operator fun invoke(uris: List<String>): PendingSystemAction = withContext(Dispatchers.IO) {
        if (uris.isEmpty()) return@withContext PendingSystemAction(null)
        val result = mediaStoreRepository.createTrashRequest(uris)
        if (result.handle == null) {
            mediaStoreRepository.deleteUrisLegacy(uris)
        }
        result
    }
}
