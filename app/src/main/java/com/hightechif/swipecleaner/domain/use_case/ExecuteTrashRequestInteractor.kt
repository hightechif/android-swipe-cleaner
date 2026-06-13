package com.hightechif.swipecleaner.domain.use_case

import android.content.IntentSender
import com.hightechif.swipecleaner.domain.repository.IMediaStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExecuteTrashRequestInteractor(
    private val mediaStoreRepository: IMediaStoreRepository
) : ExecuteTrashRequestUseCase {

    override suspend operator fun invoke(uris: List<String>): IntentSender? = withContext(Dispatchers.IO) {
        if (uris.isEmpty()) return@withContext null

        val intentSender = mediaStoreRepository.createTrashRequest(uris)
        if (intentSender == null) {
            mediaStoreRepository.deleteUrisLegacy(uris)
        }
        intentSender
    }
}
