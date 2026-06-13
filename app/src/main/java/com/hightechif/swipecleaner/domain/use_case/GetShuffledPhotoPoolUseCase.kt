package com.hightechif.swipecleaner.domain.use_case

interface GetShuffledPhotoPoolUseCase {
    suspend operator fun invoke(bucketId: String? = null): List<String>
}
