package com.hightechif.swipecleaner.ui.feature.kept

import com.google.common.truth.Truth.assertThat
import com.hightechif.swipecleaner.domain.model.KeptPhoto
import com.hightechif.swipecleaner.domain.model.MediaImage
import com.hightechif.swipecleaner.domain.use_case.GetAllMediaImagesUseCase
import com.hightechif.swipecleaner.domain.use_case.GetKeptPhotosUseCase
import com.hightechif.swipecleaner.domain.use_case.ResetKeptPhotosUseCase
import com.hightechif.swipecleaner.domain.use_case.RestoreFromKeptUseCase
import com.hightechif.swipecleaner.util.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class KeptPhotosViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK lateinit var getKeptPhotosUseCase: GetKeptPhotosUseCase
    @MockK lateinit var resetKeptPhotosUseCase: ResetKeptPhotosUseCase
    @MockK lateinit var getMediaImagesUseCase: GetAllMediaImagesUseCase
    @MockK lateinit var restoreFromKeptUseCase: RestoreFromKeptUseCase

    private lateinit var sut: KeptPhotosViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        clearAllMocks()
        every { getKeptPhotosUseCase() } returns flowOf(emptyList())
        coEvery { getMediaImagesUseCase() } returns emptyList()
        sut = buildSut()
    }

    private fun buildSut() = KeptPhotosViewModel(
        getKeptPhotosUseCase = getKeptPhotosUseCase,
        resetKeptPhotosUseCase = resetKeptPhotosUseCase,
        getMediaImagesUseCase = getMediaImagesUseCase,
        restoreFromKeptUseCase = restoreFromKeptUseCase
    )

    @Test
    fun `state keptPhotos is populated when use case emits photos`() = runTest {
        // Arrange
        val photos = listOf(KeptPhoto(uri = "content://media/1", keptAt = 1000L))
        every { getKeptPhotosUseCase() } returns flowOf(photos)
        sut = buildSut()
        advanceUntilIdle()

        // Assert
        assertThat(sut.state.value.keptPhotos).isEqualTo(photos)
    }

    @Test
    fun `state keptPhotos is empty when use case emits empty list`() = runTest {
        // Arrange
        advanceUntilIdle()

        // Assert
        assertThat(sut.state.value.keptPhotos).isEmpty()
    }

    @Test
    fun `state mediaImages is populated on init`() = runTest {
        // Arrange
        val images = listOf(MediaImage(uri = "content://media/1", bucketId = "b", bucketName = "B"))
        coEvery { getMediaImagesUseCase() } returns images
        sut = buildSut()
        advanceUntilIdle()

        // Assert
        assertThat(sut.state.value.mediaImages).isEqualTo(images)
    }

    @Test
    fun `restoreKeptPhoto calls restoreFromKeptUseCase with correct uri`() = runTest {
        // Arrange
        coJustRun { restoreFromKeptUseCase(any()) }
        advanceUntilIdle()

        // Act
        sut.restoreKeptPhoto("content://media/1")
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { restoreFromKeptUseCase("content://media/1") }
    }

    @Test
    fun `resetProgress calls resetKeptPhotosUseCase`() = runTest {
        // Arrange
        coJustRun { resetKeptPhotosUseCase() }
        advanceUntilIdle()

        // Act
        sut.resetProgress()
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { resetKeptPhotosUseCase() }
    }
}
