package com.hightechif.swipecleaner.domain.use_case

import com.hightechif.swipecleaner.domain.repository.IKeptPhotosRepository
import com.hightechif.swipecleaner.domain.repository.IMediaStoreRepository
import com.hightechif.swipecleaner.domain.repository.ITrashedPhotosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetShuffledPhotoPoolInteractor(
    private val mediaStoreRepository: IMediaStoreRepository,
    private val keptPhotosRepository: IKeptPhotosRepository,
    private val trashedPhotosRepository: ITrashedPhotosRepository
) : GetShuffledPhotoPoolUseCase {

    override suspend operator fun invoke(bucketId: String?): List<String> = withContext(Dispatchers.IO) {
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
