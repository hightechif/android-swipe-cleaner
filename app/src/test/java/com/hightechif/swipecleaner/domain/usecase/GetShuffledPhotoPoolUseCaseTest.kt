package com.hightechif.swipecleaner.domain.usecase

import com.hightechif.swipecleaner.data.db.KeptPhotoEntity
import com.hightechif.swipecleaner.data.db.TrashedPhotoEntity
import com.hightechif.swipecleaner.data.repository.KeptPhotosRepository
import com.hightechif.swipecleaner.data.repository.MediaStoreRepository
import com.hightechif.swipecleaner.data.repository.TrashedPhotosRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GetShuffledPhotoPoolUseCaseTest {

    private lateinit var mediaStoreRepository: MediaStoreRepository
    private lateinit var keptPhotosRepository: KeptPhotosRepository
    private lateinit var trashedPhotosRepository: TrashedPhotosRepository
    private lateinit var useCase: GetShuffledPhotoPoolUseCase

    @Before
    fun setUp() {
        mediaStoreRepository = mockk()
        keptPhotosRepository = mockk()
        trashedPhotosRepository = mockk()
        useCase = GetShuffledPhotoPoolUseCase(
            mediaStoreRepository,
            keptPhotosRepository,
            trashedPhotosRepository
        )
    }

    // Task 4.2: Filtering excludes kept photos
    @Test
    fun invoke_excludesKeptPhotosFromResult() = runTest {
        coEvery { mediaStoreRepository.queryAllImageUris() } returns listOf(
            "content://media/1",
            "content://media/2",
            "content://media/3"
        )
        coEvery { keptPhotosRepository.getKeptPhotos() } returns listOf(
            KeptPhotoEntity(uri = "content://media/2", keptAt = 1000L)
        )
        coEvery { trashedPhotosRepository.getTrashedPhotos() } returns emptyList()

        val result = useCase()

        assertFalse("Kept photo should be excluded", result.contains("content://media/2"))
        assertTrue(result.contains("content://media/1"))
        assertTrue(result.contains("content://media/3"))
    }

    // Task 4.2: Filtering excludes trashed/non-image files (trashed URIs)
    @Test
    fun invoke_excludesTrashedPhotosFromResult() = runTest {
        coEvery { mediaStoreRepository.queryAllImageUris() } returns listOf(
            "content://media/1",
            "content://media/2",
            "content://media/3"
        )
        coEvery { keptPhotosRepository.getKeptPhotos() } returns emptyList()
        coEvery { trashedPhotosRepository.getTrashedPhotos() } returns listOf(
            TrashedPhotoEntity(uri = "content://media/3", trashedAt = 2000L)
        )

        val result = useCase()

        assertFalse("Trashed photo should be excluded", result.contains("content://media/3"))
        assertTrue(result.contains("content://media/1"))
        assertTrue(result.contains("content://media/2"))
    }

    // Task 4.3: Returns empty list when all photos are kept or trashed
    @Test
    fun invoke_returnsEmptyList_whenAllPhotosAreFilteredOut() = runTest {
        coEvery { mediaStoreRepository.queryAllImageUris() } returns listOf(
            "content://media/1",
            "content://media/2"
        )
        coEvery { keptPhotosRepository.getKeptPhotos() } returns listOf(
            KeptPhotoEntity(uri = "content://media/1", keptAt = 1000L)
        )
        coEvery { trashedPhotosRepository.getTrashedPhotos() } returns listOf(
            TrashedPhotoEntity(uri = "content://media/2", trashedAt = 2000L)
        )

        val result = useCase()

        assertTrue("Result should be empty when all photos filtered", result.isEmpty())
    }

    // Task 4.5: Returns all images when no kept or trashed
    @Test
    fun invoke_returnsAllImages_whenNoKeptOrTrashed() = runTest {
        val allUris = listOf("content://media/1", "content://media/2", "content://media/3")
        coEvery { mediaStoreRepository.queryAllImageUris() } returns allUris
        coEvery { keptPhotosRepository.getKeptPhotos() } returns emptyList()
        coEvery { trashedPhotosRepository.getTrashedPhotos() } returns emptyList()

        val result = useCase()

        assertTrue(result.containsAll(allUris))
        assertTrue(allUris.containsAll(result))
    }

    // Task 4.5: Returns empty list when media store returns no images
    @Test
    fun invoke_returnsEmptyList_whenMediaStoreIsEmpty() = runTest {
        coEvery { mediaStoreRepository.queryAllImageUris() } returns emptyList()
        coEvery { keptPhotosRepository.getKeptPhotos() } returns emptyList()
        coEvery { trashedPhotosRepository.getTrashedPhotos() } returns emptyList()

        val result = useCase()

        assertTrue(result.isEmpty())
    }
}
