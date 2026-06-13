package com.hightechif.swipecleaner.ui.feature.swipe

import com.google.common.truth.Truth.assertThat
import com.hightechif.swipecleaner.domain.use_case.AddToTrashUseCase
import com.hightechif.swipecleaner.domain.use_case.ClearTrashedPhotosUseCase
import com.hightechif.swipecleaner.domain.use_case.ExecuteTrashRequestUseCase
import com.hightechif.swipecleaner.domain.use_case.GetAllMediaImagesUseCase
import com.hightechif.swipecleaner.domain.use_case.GetFilteredAlbumsUseCase
import com.hightechif.swipecleaner.domain.use_case.GetKeptPhotosUseCase
import com.hightechif.swipecleaner.domain.use_case.GetShuffledPhotoPoolUseCase
import com.hightechif.swipecleaner.domain.use_case.GetTrashedPhotosUseCase
import com.hightechif.swipecleaner.domain.use_case.MarkImageKeptUseCase
import com.hightechif.swipecleaner.domain.use_case.ResetKeptPhotosUseCase
import com.hightechif.swipecleaner.domain.use_case.RestoreFromKeptUseCase
import com.hightechif.swipecleaner.domain.use_case.RestoreFromTrashUseCase
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
class SwipeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK lateinit var getShuffledPhotoPoolUseCase: GetShuffledPhotoPoolUseCase
    @MockK lateinit var markImageKeptUseCase: MarkImageKeptUseCase
    @MockK lateinit var executeTrashRequestUseCase: ExecuteTrashRequestUseCase
    @MockK lateinit var getKeptPhotosUseCase: GetKeptPhotosUseCase
    @MockK lateinit var getTrashedPhotosUseCase: GetTrashedPhotosUseCase
    @MockK lateinit var addToTrashUseCase: AddToTrashUseCase
    @MockK lateinit var restoreFromTrashUseCase: RestoreFromTrashUseCase
    @MockK lateinit var clearTrashedPhotosUseCase: ClearTrashedPhotosUseCase
    @MockK lateinit var restoreFromKeptUseCase: RestoreFromKeptUseCase
    @MockK lateinit var resetKeptPhotosUseCase: ResetKeptPhotosUseCase
    @MockK lateinit var getMediaImagesUseCase: GetAllMediaImagesUseCase
    @MockK lateinit var getFilteredAlbumsUseCase: GetFilteredAlbumsUseCase

    private lateinit var sut: SwipeViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        clearAllMocks()
        stubDefaults()
        sut = buildSut()
    }

    private fun stubDefaults() {
        every { getKeptPhotosUseCase() } returns flowOf(emptyList())
        every { getTrashedPhotosUseCase() } returns flowOf(emptyList())
        every { getFilteredAlbumsUseCase(any()) } returns flowOf(emptyList())
        coEvery { getMediaImagesUseCase() } returns emptyList()
        coEvery { getShuffledPhotoPoolUseCase(any()) } returns listOf(
            "content://media/1",
            "content://media/2",
            "content://media/3"
        )
    }

    private fun buildSut() = SwipeViewModel(
        getShuffledPhotoPoolUseCase = getShuffledPhotoPoolUseCase,
        markImageKeptUseCase = markImageKeptUseCase,
        executeTrashRequestUseCase = executeTrashRequestUseCase,
        getKeptPhotosUseCase = getKeptPhotosUseCase,
        getTrashedPhotosUseCase = getTrashedPhotosUseCase,
        addToTrashUseCase = addToTrashUseCase,
        restoreFromTrashUseCase = restoreFromTrashUseCase,
        clearTrashedPhotosUseCase = clearTrashedPhotosUseCase,
        restoreFromKeptUseCase = restoreFromKeptUseCase,
        resetKeptPhotosUseCase = resetKeptPhotosUseCase,
        getMediaImagesUseCase = getMediaImagesUseCase,
        getFilteredAlbumsUseCase = getFilteredAlbumsUseCase
    )

    @Test
    fun `swipeRight increments keptCount when photo is kept`() = runTest {
        // Arrange
        coJustRun { markImageKeptUseCase(any()) }
        advanceUntilIdle()

        // Act
        sut.swipeRight()
        advanceUntilIdle()

        // Assert
        assertThat(sut.state.value.keptCount).isEqualTo(1)
    }

    @Test
    fun `swipeRight advances currentIndex after keeping photo`() = runTest {
        // Arrange
        coJustRun { markImageKeptUseCase(any()) }
        advanceUntilIdle()

        // Act
        sut.swipeRight()
        advanceUntilIdle()

        // Assert
        assertThat(sut.state.value.currentIndex).isEqualTo(1)
    }

    @Test
    fun `swipeLeft advances currentIndex after trashing photo`() = runTest {
        // Arrange
        coJustRun { addToTrashUseCase(any()) }
        advanceUntilIdle()

        // Act
        sut.swipeLeft()
        advanceUntilIdle()

        // Assert
        assertThat(sut.state.value.currentIndex).isEqualTo(1)
    }

    @Test
    fun `swipeLeft calls addToTrashUseCase with correct uri`() = runTest {
        // Arrange
        coJustRun { addToTrashUseCase(any()) }
        advanceUntilIdle()

        // Act
        sut.swipeLeft()
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { addToTrashUseCase("content://media/1") }
    }

    @Test
    fun `restoreFromTrash re-inserts uri back into photo pool`() = runTest {
        // Arrange
        coJustRun { addToTrashUseCase(any()) }
        coJustRun { restoreFromTrashUseCase(any()) }
        advanceUntilIdle()
        sut.swipeLeft()
        advanceUntilIdle()

        // Act
        sut.restoreFromTrash("content://media/1")
        advanceUntilIdle()

        // Assert
        assertThat(sut.state.value.photoPool).contains("content://media/1")
    }

    @Test
    fun `restoreFromTrash sets isSessionFinished to false after restoring last photo`() = runTest {
        // Arrange
        coEvery { getShuffledPhotoPoolUseCase(any()) } returns listOf("content://media/solo")
        coJustRun { addToTrashUseCase(any()) }
        coJustRun { restoreFromTrashUseCase(any()) }
        sut = buildSut()
        advanceUntilIdle()
        sut.swipeLeft()
        advanceUntilIdle()
        assertThat(sut.state.value.isSessionFinished).isTrue()

        // Act
        sut.restoreFromTrash("content://media/solo")
        advanceUntilIdle()

        // Assert
        assertThat(sut.state.value.isSessionFinished).isFalse()
    }

    @Test
    fun `isSessionFinished is true when all photos have been swiped`() = runTest {
        // Arrange
        coEvery { getShuffledPhotoPoolUseCase(any()) } returns listOf("content://media/only")
        coJustRun { addToTrashUseCase(any()) }
        sut = buildSut()
        advanceUntilIdle()

        // Act
        sut.swipeLeft()
        advanceUntilIdle()

        // Assert
        assertThat(sut.state.value.isSessionFinished).isTrue()
    }

    @Test
    fun `isLoading is false after photo pool loads`() = runTest {
        // Arrange — stubDefaults already stubs getShuffledPhotoPoolUseCase
        advanceUntilIdle()

        // Assert
        assertThat(sut.state.value.isLoading).isFalse()
    }
}
