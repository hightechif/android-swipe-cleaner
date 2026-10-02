package com.hightechif.swipecleaner.domain.use_case

import com.hightechif.swipecleaner.domain.repository.ITrashedPhotosRepository
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
class ClearTrashedPhotosInteractorTest {

    @MockK lateinit var trashedPhotosRepository: ITrashedPhotosRepository

    private lateinit var sut: ClearTrashedPhotosInteractor

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        clearAllMocks()
        sut = ClearTrashedPhotosInteractor(trashedPhotosRepository)
    }

    @Test
    fun `invoke delegates to repository`() = runTest {
        // Arrange
        coEvery { trashedPhotosRepository.clearAllTrashedPhotos() } returns Unit

        // Act
        sut()

        // Assert
        coVerify(exactly = 1) { trashedPhotosRepository.clearAllTrashedPhotos() }
    }
}
