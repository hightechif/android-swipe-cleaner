package com.hightechif.swipecleaner.domain.use_case

interface MarkImageKeptUseCase {
    suspend operator fun invoke(uri: String)
}
