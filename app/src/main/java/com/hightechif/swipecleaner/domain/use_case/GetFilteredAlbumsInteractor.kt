package com.hightechif.swipecleaner.domain.use_case

import com.hightechif.swipecleaner.domain.model.Album
import com.hightechif.swipecleaner.domain.model.MediaImage
import com.hightechif.swipecleaner.domain.repository.IKeptPhotosRepository
import com.hightechif.swipecleaner.domain.repository.ITrashedPhotosRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetFilteredAlbumsInteractor(
    private val keptPhotosRepository: IKeptPhotosRepository,
    private val trashedPhotosRepository: ITrashedPhotosRepository
) : GetFilteredAlbumsUseCase {

    override operator fun invoke(mediaImagesFlow: Flow<List<MediaImage>>): Flow<List<Album>> =
        combine(
            mediaImagesFlow,
            keptPhotosRepository.getKeptPhotosFlow(),
            trashedPhotosRepository.getTrashedPhotosFlow()
        ) { mediaImages, keptList, trashedList ->
            val keptUris = keptList.map { it.uri }.toSet()
            val trashedUris = trashedList.map { it.uri }.toSet()

            val albumsMap = mutableMapOf<String, AlbumAccumulator>()
            for (image in mediaImages) {
                if (image.uri !in keptUris && image.uri !in trashedUris) {
                    val acc = albumsMap.getOrPut(image.bucketId) {
                        AlbumAccumulator(image.bucketId, image.bucketName, image.uri)
                    }
                    acc.count++
                }
            }
            albumsMap.values
                .map { Album(id = it.id, name = it.name, coverPhotoUri = it.coverPhotoUri, photoCount = it.count) }
                .sortedBy { it.name }
        }

    private class AlbumAccumulator(val id: String, val name: String, val coverPhotoUri: String, var count: Int = 0)
}
