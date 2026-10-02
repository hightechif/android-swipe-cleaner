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
class RestoreFromTrashInteractorTest {

    @MockK lateinit var trashedPhotosRepository: ITrashedPhotosRepository

    private lateinit var sut: RestoreFromTrashInteractor

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        clearAllMocks()
        sut = RestoreFromTrashInteractor(trashedPhotosRepository)
    }

    @Test
    fun `invoke delegates to repository`() = runTest {
        // Arrange
        coEvery { trashedPhotosRepository.deleteTrashedPhoto("u1") } returns Unit

        // Act
        sut("u1")

        // Assert
        coVerify(exactly = 1) { trashedPhotosRepository.deleteTrashedPhoto("u1") }
    }
}
