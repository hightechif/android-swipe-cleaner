package com.hightechif.swipecleaner.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hightechif.swipecleaner.data.repository.KeptPhotosRepository
import com.hightechif.swipecleaner.data.repository.MediaStoreRepository
import com.hightechif.swipecleaner.data.repository.TrashedPhotosRepository
import com.hightechif.swipecleaner.data.repository.MediaImage
import com.hightechif.swipecleaner.data.db.KeptPhotoEntity
import com.hightechif.swipecleaner.data.db.TrashedPhotoEntity
import com.hightechif.swipecleaner.domain.usecase.ExecuteTrashRequestUseCase
import com.hightechif.swipecleaner.domain.usecase.GetShuffledPhotoPoolUseCase
import com.hightechif.swipecleaner.domain.usecase.MarkImageKeptUseCase
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SwipeViewModelTest {

    // Task 5.2: InstantTaskExecutorRule for LiveData synchronous execution
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getShuffledPhotoPoolUseCase: GetShuffledPhotoPoolUseCase
    private lateinit var markImageKeptUseCase: MarkImageKeptUseCase
    private lateinit var executeTrashRequestUseCase: ExecuteTrashRequestUseCase
    private lateinit var keptPhotosRepository: KeptPhotosRepository
    private lateinit var trashedPhotosRepository: TrashedPhotosRepository
    private lateinit var mediaStoreRepository: MediaStoreRepository

    private lateinit var viewModel: SwipeViewModel

    @Before
    fun setUp() {
        // Task 5.2: Set main dispatcher to test dispatcher
        Dispatchers.setMain(testDispatcher)

        getShuffledPhotoPoolUseCase = mockk()
        markImageKeptUseCase = mockk()
        executeTrashRequestUseCase = mockk()
        keptPhotosRepository = mockk()
        trashedPhotosRepository = mockk()
        mediaStoreRepository = mockk()

        // Stub the Flow-based observables required at ViewModel init
        every { keptPhotosRepository.getKeptPhotosFlow() } returns flowOf(emptyList())
        every { trashedPhotosRepository.getTrashedPhotosFlow() } returns flowOf(emptyList())
        every { mediaStoreRepository.queryAllAlbums() } returns emptyList()
        every { mediaStoreRepository.queryAllMediaImages() } returns emptyList()

        // Default stub for loadPhotoPool called in init
        coEvery { getShuffledPhotoPoolUseCase(any()) } returns listOf(
            "content://media/1",
            "content://media/2",
            "content://media/3"
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): SwipeViewModel = SwipeViewModel(
        getShuffledPhotoPoolUseCase,
        markImageKeptUseCase,
        executeTrashRequestUseCase,
        keptPhotosRepository,
        trashedPhotosRepository,
        mediaStoreRepository
    )

    // Task 5.3: swipeLeft() adds photo to trash repository and advances index
    @Test
    fun swipeLeft_insertsPhotoIntoTrashRepository() = runTest {
        coJustRun { trashedPhotosRepository.insertTrashedPhoto(any()) }
        val vm = createViewModel()
        advanceUntilIdle() // let init coroutine (loadPhotoPool) complete

        vm.swipeLeft()
        advanceUntilIdle()

        coVerify(exactly = 1) {
            trashedPhotosRepository.insertTrashedPhoto("content://media/1")
        }
    }

    // Task 5.3: swipeLeft() advances the currentIndex
    @Test
    fun swipeLeft_advancesCurrentIndex() = runTest {
        coJustRun { trashedPhotosRepository.insertTrashedPhoto(any()) }
        val vm = createViewModel()
        advanceUntilIdle()

        val initialIndex = vm.uiState.value.currentIndex
        vm.swipeLeft()
        advanceUntilIdle()

        assertEquals(initialIndex + 1, vm.uiState.value.currentIndex)
    }

    // Task 5.4: swipeRight() keeps photo and advances index without calling trash repository
    @Test
    fun swipeRight_doesNotCallTrashRepository() = runTest {
        coJustRun { markImageKeptUseCase(any()) }
        val vm = createViewModel()
        advanceUntilIdle()

        vm.swipeRight()
        advanceUntilIdle()

        coVerify(exactly = 0) { trashedPhotosRepository.insertTrashedPhoto(any()) }
    }

    // Task 5.4: swipeRight() increments keptCount
    @Test
    fun swipeRight_incrementsKeptCount() = runTest {
        coJustRun { markImageKeptUseCase(any()) }
        val vm = createViewModel()
        advanceUntilIdle()

        val initialKeptCount = vm.uiState.value.keptCount
        vm.swipeRight()
        advanceUntilIdle()

        assertEquals(initialKeptCount + 1, vm.uiState.value.keptCount)
    }

    // Task 5.5: restoreFromTrash() calls deleteTrashedPhoto and re-inserts photo into pool
    @Test
    fun restoreFromTrash_removesFromTrashAndReInsertsIntoPool() = runTest {
        coJustRun { trashedPhotosRepository.insertTrashedPhoto(any()) }
        coJustRun { trashedPhotosRepository.deleteTrashedPhoto(any()) }
        val vm = createViewModel()
        advanceUntilIdle()

        // First swipe to move past index 0
        vm.swipeLeft()
        advanceUntilIdle()

        // Now restore content://media/1 from trash
        vm.restoreFromTrash("content://media/1")
        advanceUntilIdle()

        coVerify(exactly = 1) {
            trashedPhotosRepository.deleteTrashedPhoto("content://media/1")
        }
        // Photo should be back in the pool
        assertTrue(vm.uiState.value.photoPool.contains("content://media/1"))
    }

    // Task 5.5: restoreFromTrash() marks session as not finished
    @Test
    fun restoreFromTrash_setsSessionNotFinished() = runTest {
        coJustRun { trashedPhotosRepository.insertTrashedPhoto(any()) }
        coJustRun { trashedPhotosRepository.deleteTrashedPhoto(any()) }
        // Simulate a single-photo pool so session finishes after swipe
        coEvery { getShuffledPhotoPoolUseCase(any()) } returns listOf("content://media/solo")

        val vm = createViewModel()
        advanceUntilIdle()

        vm.swipeLeft()
        advanceUntilIdle()
        assertTrue(
            "Session should be finished after all photos swiped",
            vm.uiState.value.isSessionFinished
        )

        vm.restoreFromTrash("content://media/solo")
        advanceUntilIdle()

        assertFalse(
            "Session should not be finished after restore",
            vm.uiState.value.isSessionFinished
        )
    }

    @Test
    fun albums_computedCorrectlyFromUnreviewedPhotos() = runTest {
        val mediaImages = listOf(
            MediaImage("content://media/a1", "a", "Album A"),
            MediaImage("content://media/a2", "a", "Album A"),
            MediaImage("content://media/b1", "b", "Album B"),
            MediaImage("content://media/b2", "b", "Album B"),
            MediaImage("content://media/b3", "b", "Album B")
        )
        every { mediaStoreRepository.queryAllMediaImages() } returns mediaImages

        val keptPhotosList = listOf(
            KeptPhotoEntity("content://media/a1", 1000L)
        )
        every { keptPhotosRepository.getKeptPhotosFlow() } returns flowOf(keptPhotosList)

        val trashedPhotosList = listOf(
            TrashedPhotoEntity("content://media/b1", 2000L),
            TrashedPhotoEntity("content://media/b2", 3000L)
        )
        every { trashedPhotosRepository.getTrashedPhotosFlow() } returns flowOf(trashedPhotosList)

        val vm = createViewModel()
        advanceUntilIdle()

        val computedAlbums = vm.uiState.value.albums
        assertEquals(2, computedAlbums.size)

        val albumA = computedAlbums.find { it.id == "a" }
        val albumB = computedAlbums.find { it.id == "b" }

        assertTrue(albumA != null)
        assertEquals("Album A", albumA?.name)
        assertEquals("content://media/a2", albumA?.coverPhotoUri)
        assertEquals(1, albumA?.photoCount)

        assertTrue(albumB != null)
        assertEquals("Album B", albumB?.name)
        assertEquals("content://media/b3", albumB?.coverPhotoUri)
        assertEquals(1, albumB?.photoCount)
    }

    @Test
    fun albums_excludesFullyReviewedAlbums() = runTest {
        val mediaImages = listOf(
            MediaImage("content://media/a1", "a", "Album A"),
            MediaImage("content://media/b1", "b", "Album B")
        )
        every { mediaStoreRepository.queryAllMediaImages() } returns mediaImages

        val keptPhotosList = listOf(
            KeptPhotoEntity("content://media/a1", 1000L)
        )
        every { keptPhotosRepository.getKeptPhotosFlow() } returns flowOf(keptPhotosList)
        every { trashedPhotosRepository.getTrashedPhotosFlow() } returns flowOf(emptyList())

        val vm = createViewModel()
        advanceUntilIdle()

        val computedAlbums = vm.uiState.value.albums
        assertEquals(1, computedAlbums.size)

        val albumA = computedAlbums.find { it.id == "a" }
        val albumB = computedAlbums.find { it.id == "b" }

        assertTrue(albumA == null)
        assertTrue(albumB != null)
        assertEquals("Album B", albumB?.name)
        assertEquals("content://media/b1", albumB?.coverPhotoUri)
        assertEquals(1, albumB?.photoCount)
    }
}
