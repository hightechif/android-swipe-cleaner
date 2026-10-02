package com.hightechif.swipecleaner.domain.use_case

import com.google.common.truth.Truth.assertThat
import com.hightechif.swipecleaner.domain.model.MediaImage
import com.hightechif.swipecleaner.domain.repository.IMediaStoreRepository
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
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
class GetAllMediaImagesInteractorTest {

    @MockK lateinit var mediaStoreRepository: IMediaStoreRepository

    private lateinit var sut: GetAllMediaImagesInteractor

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        clearAllMocks()
        sut = GetAllMediaImagesInteractor(mediaStoreRepository)
    }

    @Test
    fun `invoke returns media images from repository`() = runTest {
        // Arrange
        val images = listOf(MediaImage("u1", "b1", "Camera"))
        every { mediaStoreRepository.queryAllMediaImages() } returns images

        // Act
        val result = sut()

        // Assert
        assertThat(result).isEqualTo(images)
    }

    @Test
    fun `invoke returns empty list when repository has no images`() = runTest {
        // Arrange
        every { mediaStoreRepository.queryAllMediaImages() } returns emptyList()

        // Act
        val result = sut()

        // Assert
        assertThat(result).isEmpty()
    }
}
