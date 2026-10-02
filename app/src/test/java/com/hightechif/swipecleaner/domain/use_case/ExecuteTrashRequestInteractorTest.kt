package com.hightechif.swipecleaner.domain.use_case

import com.google.common.truth.Truth.assertThat
import com.hightechif.swipecleaner.domain.model.PendingSystemAction
import com.hightechif.swipecleaner.domain.repository.IMediaStoreRepository
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ExecuteTrashRequestInteractorTest {

    @MockK lateinit var mediaStoreRepository: IMediaStoreRepository

    private lateinit var sut: ExecuteTrashRequestInteractor

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        clearAllMocks()
        sut = ExecuteTrashRequestInteractor(mediaStoreRepository)
    }

    @Test
    fun `invoke with empty list returns empty action without touching media store`() = runTest {
        // Arrange / Act
        val result = sut(emptyList())

        // Assert
        assertThat(result.handle).isNull()
        verify(exactly = 0) { mediaStoreRepository.createTrashRequest(any()) }
    }

    @Test
    fun `invoke returns request handle and skips legacy delete when request is created`() = runTest {
        // Arrange
        val action = PendingSystemAction("handle")
        every { mediaStoreRepository.createTrashRequest(listOf("u1")) } returns action

        // Act
        val result = sut(listOf("u1"))

        // Assert
        assertThat(result).isEqualTo(action)
        verify(exactly = 0) { mediaStoreRepository.deleteUrisLegacy(any()) }
    }

    @Test
    fun `invoke falls back to legacy delete when no request handle is available`() = runTest {
        // Arrange
        every { mediaStoreRepository.createTrashRequest(listOf("u1")) } returns PendingSystemAction(null)
        every { mediaStoreRepository.deleteUrisLegacy(listOf("u1")) } returns true

        // Act
        val result = sut(listOf("u1"))

        // Assert
        assertThat(result.handle).isNull()
        verify(exactly = 1) { mediaStoreRepository.deleteUrisLegacy(listOf("u1")) }
    }
}
