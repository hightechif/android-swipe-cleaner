package com.hightechif.swipecleaner.domain.usecase

import com.hightechif.swipecleaner.data.repository.KeptPhotosRepository
import com.hightechif.swipecleaner.data.repository.MediaStoreRepository
import com.hightechif.swipecleaner.data.repository.TrashedPhotosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetShuffledPhotoPoolUseCase(
    private val mediaStoreRepository: MediaStoreRepository,
    private val keptPhotosRepository: KeptPhotosRepository,
    private val trashedPhotosRepository: TrashedPhotosRepository
) {
    suspend operator fun invoke(bucketId: String? = null): List<String> = withContext(Dispatchers.IO) {
        val allImages = if (bucketId != null) {
            mediaStoreRepository.queryImageUrisFromBucket(bucketId)
        } else {
            mediaStoreRepository.queryAllImageUris()
        }
        val keptImages = keptPhotosRepository.getKeptPhotos().map { it.uri }.toSet()
        val trashedImages = trashedPhotosRepository.getTrashedPhotos().map { it.uri }.toSet()
        
        allImages.filter { it !in keptImages && it !in trashedImages }.shuffled()
    }
}
