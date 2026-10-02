package com.hightechif.swipecleaner.data.repository

import com.google.common.truth.Truth.assertThat
import com.hightechif.swipecleaner.data.source.local.KeptPhotoDao
import com.hightechif.swipecleaner.data.source.local.KeptPhotoEntity
import com.hightechif.swipecleaner.domain.model.KeptPhoto
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
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
class KeptPhotosRepositoryTest {

    @MockK lateinit var dao: KeptPhotoDao

    private lateinit var sut: KeptPhotosRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        clearAllMocks()
        sut = KeptPhotosRepository(dao)
    }

    @Test
    fun `insertKeptPhoto stores entity with given uri and a timestamp`() = runTest {
        // Arrange
        val slot = slot<KeptPhotoEntity>()
        coJustRun { dao.insertKeptPhoto(capture(slot)) }

        // Act
        sut.insertKeptPhoto("content://media/1")

        // Assert
        assertThat(slot.captured.uri).isEqualTo("content://media/1")
        assertThat(slot.captured.keptAt).isGreaterThan(0L)
    }

    @Test
    fun `deleteKeptPhoto delegates uri to dao`() = runTest {
        // Arrange
        coJustRun { dao.deleteKeptPhoto(any()) }

        // Act
        sut.deleteKeptPhoto("content://media/2")

        // Assert
        coVerify(exactly = 1) { dao.deleteKeptPhoto("content://media/2") }
    }

    @Test
    fun `clearAllKeptPhotos delegates to dao deleteAll`() = runTest {
        // Arrange
        coJustRun { dao.deleteAllKeptPhotos() }

        // Act
        sut.clearAllKeptPhotos()

        // Assert
        coVerify(exactly = 1) { dao.deleteAllKeptPhotos() }
    }

    @Test
    fun `getKeptPhotos returns mapped domain models from dao`() = runTest {
        // Arrange
        coEvery { dao.getAllKeptPhotos() } returns listOf(KeptPhotoEntity("u1", 10L))

        // Act
        val result = sut.getKeptPhotos()

        // Assert
        assertThat(result).containsExactly(KeptPhoto("u1", 10L))
    }

    @Test
    fun `getKeptPhotos returns empty list when dao returns empty`() = runTest {
        // Arrange
        coEvery { dao.getAllKeptPhotos() } returns emptyList()

        // Act
        val result = sut.getKeptPhotos()

        // Assert
        assertThat(result).isEmpty()
    }

    @Test
    fun `getKeptPhotosFlow emits mapped domain models from dao`() = runTest {
        // Arrange
        every { dao.getAllKeptPhotosFlow() } returns flowOf(listOf(KeptPhotoEntity("u1", 10L)))

        // Act
        val result = sut.getKeptPhotosFlow().first()

        // Assert
        assertThat(result).containsExactly(KeptPhoto("u1", 10L))
    }
}
