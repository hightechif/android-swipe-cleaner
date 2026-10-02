package com.hightechif.swipecleaner.ui.feature.trash

import com.google.common.truth.Truth.assertThat
import com.hightechif.swipecleaner.domain.model.PendingSystemAction
import com.hightechif.swipecleaner.domain.model.TrashedPhoto
import com.hightechif.swipecleaner.domain.use_case.ClearTrashedPhotosUseCase
import com.hightechif.swipecleaner.domain.use_case.ExecuteTrashRequestUseCase
import com.hightechif.swipecleaner.domain.use_case.GetTrashedPhotosUseCase
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class TrashViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK lateinit var getTrashedPhotosUseCase: GetTrashedPhotosUseCase
    @MockK lateinit var executeTrashRequestUseCase: ExecuteTrashRequestUseCase
    @MockK lateinit var clearTrashedPhotosUseCase: ClearTrashedPhotosUseCase
    @MockK lateinit var restoreFromTrashUseCase: RestoreFromTrashUseCase

    private lateinit var sut: TrashViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        clearAllMocks()
        every { getTrashedPhotosUseCase() } returns flowOf(listOf(TrashedPhoto("u1", 1L), TrashedPhoto("u2", 2L)))
        sut = TrashViewModel(
            getTrashedPhotosUseCase = getTrashedPhotosUseCase,
            executeTrashRequestUseCase = executeTrashRequestUseCase,
            clearTrashedPhotosUseCase = clearTrashedPhotosUseCase,
            restoreFromTrashUseCase = restoreFromTrashUseCase
        )
    }

    @Test
    fun `state deleteQueue mirrors trashed photos`() = runTest {
        // Arrange / Act
        advanceUntilIdle()

        // Assert
        assertThat(sut.state.value.deleteQueue).containsExactly("u1", "u2").inOrder()
    }

    @Test
    fun `executeTrashRequest emits event when system handle is returned`() = runTest {
        // Arrange
        val action = PendingSystemAction("handle")
        coEvery { executeTrashRequestUseCase(listOf("u1", "u2")) } returns action
        val received = mutableListOf<PendingSystemAction>()
        val job = launch(mainDispatcherRule.testDispatcher) { sut.trashEvent.collect { received.add(it) } }

        // Act
        sut.executeTrashRequest()
        advanceUntilIdle()

        // Assert
        assertThat(received).containsExactly(action)
        job.cancel()
    }

    @Test
    fun `executeTrashRequest emits nothing when no system handle is returned`() = runTest {
        // Arrange
        coEvery { executeTrashRequestUseCase(any()) } returns PendingSystemAction(null)
        val received = mutableListOf<PendingSystemAction>()
        val job = launch(mainDispatcherRule.testDispatcher) { sut.trashEvent.collect { received.add(it) } }

        // Act
        sut.executeTrashRequest()
        advanceUntilIdle()

        // Assert
        assertThat(received).isEmpty()
        job.cancel()
    }

    @Test
    fun `onTrashRequestCompleted clears trashed photos`() = runTest {
        // Arrange
        coJustRun { clearTrashedPhotosUseCase() }

        // Act
        sut.onTrashRequestCompleted()
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { clearTrashedPhotosUseCase() }
    }

    @Test
    fun `restoreFromTrash invokes callback after successful restore`() = runTest {
        // Arrange
        coJustRun { restoreFromTrashUseCase("u1") }
        var restored = false

        // Act
        sut.restoreFromTrash("u1") { restored = true }
        advanceUntilIdle()

        // Assert
        assertThat(restored).isTrue()
    }

    @Test
    fun `restoreFromTrash skips callback when restore fails`() = runTest {
        // Arrange
        coEvery { restoreFromTrashUseCase("u1") } throws IllegalStateException("boom")
        var restored = false

        // Act
        sut.restoreFromTrash("u1") { restored = true }
        advanceUntilIdle()

        // Assert
        assertThat(restored).isFalse()
    }
}
