package com.hightechif.swipecleaner.domain.use_case

import android.content.IntentSender

interface ExecuteTrashRequestUseCase {
    suspend operator fun invoke(uris: List<String>): IntentSender?
}
