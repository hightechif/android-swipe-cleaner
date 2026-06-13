package com.hightechif.swipecleaner.domain.use_case

interface RestoreFromTrashUseCase {
    suspend operator fun invoke(uri: String)
}
