package com.hightechif.swipecleaner.domain.use_case

import com.hightechif.swipecleaner.domain.model.PendingSystemAction

interface ExecuteTrashRequestUseCase {
    suspend operator fun invoke(uris: List<String>): PendingSystemAction
}
