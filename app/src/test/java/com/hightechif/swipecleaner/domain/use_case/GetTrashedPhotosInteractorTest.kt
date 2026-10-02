package com.hightechif.swipecleaner.domain.use_case

import com.google.common.truth.Truth.assertThat
import com.hightechif.swipecleaner.domain.model.TrashedPhoto
import com.hightechif.swipecleaner.domain.repository.ITrashedPhotosRepository
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class GetTrashedPhotosInteractorTest {

    @MockK lateinit var trashedPhotosRepository: ITrashedPhotosRepository

    private lateinit var sut: GetTrashedPhotosInteractor

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        clearAllMocks()
        sut = GetTrashedPhotosInteractor(trashedPhotosRepository)
    }

    @Test
    fun `invoke emits repository flow`() = runTest {
        // Arrange
        every { trashedPhotosRepository.getTrashedPhotosFlow() } returns flowOf(listOf(TrashedPhoto("u1", 1L)))

        // Act
        val result = sut().first()

        // Assert
        assertThat(result).isEqualTo(listOf(TrashedPhoto("u1", 1L)))
    }

    @Test
    fun `invoke emits empty list when repository has none`() = runTest {
        // Arrange
        every { trashedPhotosRepository.getTrashedPhotosFlow() } returns flowOf(emptyList())

        // Act
        val result = sut().first()

        // Assert
        assertThat(result).isEmpty()
    }
}
