package com.hightechif.swipecleaner.domain.use_case

import com.google.common.truth.Truth.assertThat
import com.hightechif.swipecleaner.domain.model.Album
import com.hightechif.swipecleaner.domain.model.KeptPhoto
import com.hightechif.swipecleaner.domain.model.MediaImage
import com.hightechif.swipecleaner.domain.model.TrashedPhoto
import com.hightechif.swipecleaner.domain.repository.IKeptPhotosRepository
import com.hightechif.swipecleaner.domain.repository.ITrashedPhotosRepository
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GetFilteredAlbumsInteractorTest {

    @MockK lateinit var keptPhotosRepository: IKeptPhotosRepository
    @MockK lateinit var trashedPhotosRepository: ITrashedPhotosRepository

    private lateinit var sut: GetFilteredAlbumsInteractor

    private val images = listOf(
        MediaImage("u1", "b1", "Camera"),
        MediaImage("u2", "b1", "Camera"),
        MediaImage("u3", "b2", "Apps")
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        clearAllMocks()
        sut = GetFilteredAlbumsInteractor(keptPhotosRepository, trashedPhotosRepository)
    }

    @Test
    fun `invoke counts photos per album sorted by name when nothing is filtered`() = runTest {
        // Arrange
        every { keptPhotosRepository.getKeptPhotosFlow() } returns flowOf(emptyList())
        every { trashedPhotosRepository.getTrashedPhotosFlow() } returns flowOf(emptyList())

        // Act
        val result = sut(flowOf(images)).first()

        // Assert
        assertThat(result).containsExactly(
            Album("b2", "Apps", "u3", 1),
            Album("b1", "Camera", "u1", 2)
        ).inOrder()
    }

    @Test
    fun `invoke excludes kept and trashed photos and drops emptied albums`() = runTest {
        // Arrange
        every { keptPhotosRepository.getKeptPhotosFlow() } returns flowOf(listOf(KeptPhoto("u1", 1L)))
        every { trashedPhotosRepository.getTrashedPhotosFlow() } returns flowOf(listOf(TrashedPhoto("u3", 2L)))

        // Act
        val result = sut(flowOf(images)).first()

        // Assert
        assertThat(result).containsExactly(Album("b1", "Camera", "u2", 1))
    }
}
