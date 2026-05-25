package com.hightechif.swipecleaner.domain.usecase

import android.content.IntentSender
import com.hightechif.swipecleaner.data.repository.MediaStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExecuteTrashRequestUseCase(
    private val mediaStoreRepository: MediaStoreRepository
) {
    suspend operator fun invoke(uris: List<String>): IntentSender? = withContext(Dispatchers.IO) {
        if (uris.isEmpty()) return@withContext null
        
        val intentSender = mediaStoreRepository.createTrashRequest(uris)
        if (intentSender == null) {
            // Legacy direct delete fallback
            mediaStoreRepository.deleteUrisLegacy(uris)
        }
        intentSender
    }
}
