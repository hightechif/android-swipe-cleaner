package com.hightechif.swipecleaner.domain.use_case

interface RestoreFromKeptUseCase {
    suspend operator fun invoke(uri: String)
}
