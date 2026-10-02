package com.hightechif.swipecleaner.ui.feature.swipe

import com.google.common.truth.Truth.assertThat
import com.hightechif.swipecleaner.domain.use_case.AddToTrashUseCase
import com.hightechif.swipecleaner.domain.use_case.GetAllMediaImagesUseCase
import com.hightechif.swipecleaner.domain.use_case.GetFilteredAlbumsUseCase
import com.hightechif.swipecleaner.domain.use_case.GetShuffledPhotoPoolUseCase
import com.hightechif.swipecleaner.domain.use_case.MarkImageKeptUseCase
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
    @MockK lateinit var addToTrashUseCase: AddToTrashUseCase
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
        addToTrashUseCase = addToTrashUseCase,
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
    fun `onPhotoRestored re-inserts uri at the current position`() = runTest {
        // Arrange
        coJustRun { addToTrashUseCase(any()) }
        advanceUntilIdle()
        sut.swipeLeft()
        advanceUntilIdle()

        // Act
        sut.onPhotoRestored("content://media/1", wasKept = false)

        // Assert
        assertThat(sut.state.value.photoPool[sut.state.value.currentIndex]).isEqualTo("content://media/1")
        assertThat(sut.state.value.sessionSwipeCount).isEqualTo(0)
    }

    @Test
    fun `onPhotoRestored decrements keptCount only when photo was kept`() = runTest {
        // Arrange
        coJustRun { markImageKeptUseCase(any()) }
        advanceUntilIdle()
        sut.swipeRight()
        advanceUntilIdle()

        // Act
        sut.onPhotoRestored("content://media/1", wasKept = true)

        // Assert
        assertThat(sut.state.value.keptCount).isEqualTo(0)
    }

    @Test
    fun `onPhotoRestored keeps keptCount when photo came from trash`() = runTest {
        // Arrange
        coJustRun { markImageKeptUseCase(any()) }
        advanceUntilIdle()
        sut.swipeRight()
        advanceUntilIdle()

        // Act
        sut.onPhotoRestored("content://media/9", wasKept = false)

        // Assert
        assertThat(sut.state.value.keptCount).isEqualTo(1)
    }

    @Test
    fun `onPhotoRestored sets isSessionFinished to false after restoring last photo`() = runTest {
        // Arrange
        coEvery { getShuffledPhotoPoolUseCase(any()) } returns listOf("content://media/solo")
        coJustRun { addToTrashUseCase(any()) }
        sut = buildSut()
        advanceUntilIdle()
        sut.swipeLeft()
        advanceUntilIdle()
        assertThat(sut.state.value.isSessionFinished).isTrue()

        // Act
        sut.onPhotoRestored("content://media/solo", wasKept = false)

        // Assert
        assertThat(sut.state.value.isSessionFinished).isFalse()
    }

    @Test
    fun `onAllKeptPhotosReset reloads pool and returns to swipe tab`() = runTest {
        // Arrange
        advanceUntilIdle()
        sut.setActiveTab(SwipeTab.KEPT)

        // Act
        sut.onAllKeptPhotosReset()
        advanceUntilIdle()

        // Assert
        assertThat(sut.state.value.activeTab).isEqualTo(SwipeTab.SWIPE)
        coVerify(atLeast = 2) { getShuffledPhotoPoolUseCase(any()) }
    }

    @Test
    fun `milestone dialog shows on the 50th swipe and can be dismissed`() = runTest {
        // Arrange
        coEvery { getShuffledPhotoPoolUseCase(any()) } returns List(60) { "content://media/$it" }
        coJustRun { addToTrashUseCase(any()) }
        sut = buildSut()
        advanceUntilIdle()

        // Act
        repeat(50) {
            sut.swipeLeft()
            advanceUntilIdle()
        }

        // Assert
        assertThat(sut.state.value.showMilestoneDialog).isTrue()
        sut.dismissMilestoneDialog()
        assertThat(sut.state.value.showMilestoneDialog).isFalse()
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
