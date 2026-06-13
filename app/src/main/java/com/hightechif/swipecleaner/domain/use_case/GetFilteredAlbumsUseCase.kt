package com.hightechif.swipecleaner.domain.use_case

import com.hightechif.swipecleaner.domain.model.Album
import com.hightechif.swipecleaner.domain.model.MediaImage
import kotlinx.coroutines.flow.Flow

interface GetFilteredAlbumsUseCase {
    operator fun invoke(mediaImagesFlow: Flow<List<MediaImage>>): Flow<List<Album>>
}
