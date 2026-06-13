package com.hightechif.swipecleaner.domain.use_case

import com.google.common.truth.Truth.assertThat
import com.hightechif.swipecleaner.domain.model.KeptPhoto
import com.hightechif.swipecleaner.domain.model.TrashedPhoto
import com.hightechif.swipecleaner.domain.repository.IKeptPhotosRepository
import com.hightechif.swipecleaner.domain.repository.IMediaStoreRepository
import com.hightechif.swipecleaner.domain.repository.ITrashedPhotosRepository
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class GetShuffledPhotoPoolInteractorTest {

    @MockK lateinit var mediaStoreRepository: IMediaStoreRepository
    @MockK lateinit var keptPhotosRepository: IKeptPhotosRepository
    @MockK lateinit var trashedPhotosRepository: ITrashedPhotosRepository

    private lateinit var sut: GetShuffledPhotoPoolInteractor

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        clearAllMocks()
        sut = GetShuffledPhotoPoolInteractor(
            mediaStoreRepository = mediaStoreRepository,
            keptPhotosRepository = keptPhotosRepository,
            trashedPhotosRepository = trashedPhotosRepository
        )
    }

    @Test
    fun `invoke excludes kept photos from result`() = runTest {
        // Arrange
        every { mediaStoreRepository.queryAllImageUris() } returns listOf(
            "content://media/1",
            "content://media/2",
            "content://media/3"
        )
        coEvery { keptPhotosRepository.getKeptPhotos() } returns listOf(
            KeptPhoto(uri = "content://media/2", keptAt = 1000L)
        )
        coEvery { trashedPhotosRepository.getTrashedPhotos() } returns emptyList()

        // Act
        val result = sut()

        // Assert
        assertThat(result).doesNotContain("content://media/2")
        assertThat(result).contains("content://media/1")
        assertThat(result).contains("content://media/3")
    }

    @Test
    fun `invoke excludes trashed photos from result`() = runTest {
        // Arrange
        every { mediaStoreRepository.queryAllImageUris() } returns listOf(
            "content://media/1",
            "content://media/2",
            "content://media/3"
        )
        coEvery { keptPhotosRepository.getKeptPhotos() } returns emptyList()
        coEvery { trashedPhotosRepository.getTrashedPhotos() } returns listOf(
            TrashedPhoto(uri = "content://media/3", trashedAt = 2000L)
        )

        // Act
        val result = sut()

        // Assert
        assertThat(result).doesNotContain("content://media/3")
        assertThat(result).contains("content://media/1")
        assertThat(result).contains("content://media/2")
    }

    @Test
    fun `invoke returns empty list when all photos are kept or trashed`() = runTest {
        // Arrange
        every { mediaStoreRepository.queryAllImageUris() } returns listOf(
            "content://media/1",
            "content://media/2"
        )
        coEvery { keptPhotosRepository.getKeptPhotos() } returns listOf(
            KeptPhoto(uri = "content://media/1", keptAt = 1000L)
        )
        coEvery { trashedPhotosRepository.getTrashedPhotos() } returns listOf(
            TrashedPhoto(uri = "content://media/2", trashedAt = 2000L)
        )

        // Act
        val result = sut()

        // Assert
        assertThat(result).isEmpty()
    }

    @Test
    fun `invoke returns all images when no photos are kept or trashed`() = runTest {
        // Arrange
        val allUris = listOf("content://media/1", "content://media/2", "content://media/3")
        every { mediaStoreRepository.queryAllImageUris() } returns allUris
        coEvery { keptPhotosRepository.getKeptPhotos() } returns emptyList()
        coEvery { trashedPhotosRepository.getTrashedPhotos() } returns emptyList()

        // Act
        val result = sut()

        // Assert
        assertThat(result).containsExactlyElementsIn(allUris)
    }

    @Test
    fun `invoke returns empty list when media store has no images`() = runTest {
        // Arrange
        every { mediaStoreRepository.queryAllImageUris() } returns emptyList()
        coEvery { keptPhotosRepository.getKeptPhotos() } returns emptyList()
        coEvery { trashedPhotosRepository.getTrashedPhotos() } returns emptyList()

        // Act
        val result = sut()

        // Assert
        assertThat(result).isEmpty()
    }
}
