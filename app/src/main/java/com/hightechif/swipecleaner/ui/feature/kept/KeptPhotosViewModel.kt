package com.hightechif.swipecleaner.ui.feature.kept

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hightechif.swipecleaner.domain.model.KeptPhoto
import com.hightechif.swipecleaner.domain.model.MediaImage
import com.hightechif.swipecleaner.domain.use_case.GetAllMediaImagesUseCase
import com.hightechif.swipecleaner.domain.use_case.GetKeptPhotosUseCase
import com.hightechif.swipecleaner.domain.use_case.ResetKeptPhotosUseCase
import com.hightechif.swipecleaner.domain.use_case.RestoreFromKeptUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

data class KeptPhotosScreenState(
    val keptPhotos: List<KeptPhoto> = emptyList(),
    val mediaImages: List<MediaImage> = emptyList()
)

class KeptPhotosViewModel(
    getKeptPhotosUseCase: GetKeptPhotosUseCase,
    private val resetKeptPhotosUseCase: ResetKeptPhotosUseCase,
    private val getMediaImagesUseCase: GetAllMediaImagesUseCase,
    private val restoreFromKeptUseCase: RestoreFromKeptUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(KeptPhotosScreenState())
    val state: StateFlow<KeptPhotosScreenState> = _state.asStateFlow()

    val resolvedKeptPhotos: StateFlow<List<ResolvedKeptPhoto>> = combine(
        _state.map { it.keptPhotos },
        _state.map { it.mediaImages }
    ) { kept, mediaImages ->
        val mediaMap = mediaImages.associateBy { it.uri }
        kept.map { k ->
            val m = mediaMap[k.uri]
            ResolvedKeptPhoto(k.uri, k.keptAt, m?.bucketId ?: "unknown", m?.bucketName ?: "Others")
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val keptAlbums: StateFlow<List<KeptAlbum>> = resolvedKeptPhotos.map { photos ->
        photos.groupBy { it.bucketId }.map { (bucketId, albumPhotos) ->
            KeptAlbum(
                id = bucketId,
                name = albumPhotos.first().bucketName,
                coverPhotoUri = albumPhotos.first().uri,
                photoCount = albumPhotos.size,
                photos = albumPhotos
            )
        }.sortedBy { it.name }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            getKeptPhotosUseCase().collect { photos ->
                _state.update { it.copy(keptPhotos = photos) }
            }
        }
        viewModelScope.launch {
            try {
                val images = getMediaImagesUseCase()
                _state.update { it.copy(mediaImages = images) }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load media images")
            }
        }
    }

    fun resetProgress(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            resetKeptPhotosUseCase()
            onComplete()
        }
    }

    fun restoreKeptPhoto(uri: String) {
        viewModelScope.launch {
            try {
                restoreFromKeptUseCase(uri)
            } catch (e: Exception) {
                Timber.e(e, "Failed to restore kept photo")
            }
        }
    }
}
