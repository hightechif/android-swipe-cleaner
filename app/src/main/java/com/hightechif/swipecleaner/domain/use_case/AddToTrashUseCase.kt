package com.hightechif.swipecleaner.domain.use_case

interface AddToTrashUseCase {
    suspend operator fun invoke(uri: String)
}
