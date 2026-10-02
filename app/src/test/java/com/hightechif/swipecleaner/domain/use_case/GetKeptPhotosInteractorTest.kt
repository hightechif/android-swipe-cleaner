package com.hightechif.swipecleaner.domain.use_case

import com.google.common.truth.Truth.assertThat
import com.hightechif.swipecleaner.domain.model.KeptPhoto
import com.hightechif.swipecleaner.domain.repository.IKeptPhotosRepository
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
class GetKeptPhotosInteractorTest {

    @MockK lateinit var keptPhotosRepository: IKeptPhotosRepository

    private lateinit var sut: GetKeptPhotosInteractor

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        clearAllMocks()
        sut = GetKeptPhotosInteractor(keptPhotosRepository)
    }

    @Test
    fun `invoke emits repository flow`() = runTest {
        // Arrange
        every { keptPhotosRepository.getKeptPhotosFlow() } returns flowOf(listOf(KeptPhoto("u1", 1L)))

        // Act
        val result = sut().first()

        // Assert
        assertThat(result).isEqualTo(listOf(KeptPhoto("u1", 1L)))
    }

    @Test
    fun `invoke emits empty list when repository has none`() = runTest {
        // Arrange
        every { keptPhotosRepository.getKeptPhotosFlow() } returns flowOf(emptyList())

        // Act
        val result = sut().first()

        // Assert
        assertThat(result).isEmpty()
    }
}
