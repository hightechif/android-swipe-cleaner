package com.hightechif.swipecleaner.domain.use_case

import com.hightechif.swipecleaner.domain.repository.IKeptPhotosRepository
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RestoreFromKeptInteractorTest {

    @MockK lateinit var keptPhotosRepository: IKeptPhotosRepository

    private lateinit var sut: RestoreFromKeptInteractor

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        clearAllMocks()
        sut = RestoreFromKeptInteractor(keptPhotosRepository)
    }

    @Test
    fun `invoke delegates to repository`() = runTest {
        // Arrange
        coEvery { keptPhotosRepository.deleteKeptPhoto("u1") } returns Unit

        // Act
        sut("u1")

        // Assert
        coVerify(exactly = 1) { keptPhotosRepository.deleteKeptPhoto("u1") }
    }
}
