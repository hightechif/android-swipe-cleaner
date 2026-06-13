package com.hightechif.swipecleaner.data.repository

import com.google.common.truth.Truth.assertThat
import com.hightechif.swipecleaner.data.source.local.TrashedPhotoDao
import com.hightechif.swipecleaner.data.source.local.TrashedPhotoEntity
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
class TrashedPhotosRepositoryTest {

    @MockK lateinit var dao: TrashedPhotoDao

    private lateinit var sut: TrashedPhotosRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        clearAllMocks()
        sut = TrashedPhotosRepository(dao)
    }

    @Test
    fun `insertTrashedPhoto calls dao insert with correct uri`() = runTest {
        // Arrange
        val slot = slot<TrashedPhotoEntity>()
        coJustRun { dao.insertTrashedPhoto(capture(slot)) }

        // Act
        sut.insertTrashedPhoto("content://media/test/1")

        // Assert
        coVerify(exactly = 1) { dao.insertTrashedPhoto(any()) }
        assertThat(slot.captured.uri).isEqualTo("content://media/test/1")
    }

    @Test
    fun `deleteTrashedPhoto calls dao delete with correct uri`() = runTest {
        // Arrange
        val uriSlot = slot<String>()
        coJustRun { dao.deleteTrashedPhoto(capture(uriSlot)) }

        // Act
        sut.deleteTrashedPhoto("content://media/test/2")

        // Assert
        coVerify(exactly = 1) { dao.deleteTrashedPhoto("content://media/test/2") }
        assertThat(uriSlot.captured).isEqualTo("content://media/test/2")
    }

    @Test
    fun `getTrashedPhotos returns mapped domain models from dao`() = runTest {
        // Arrange
        val entities = listOf(
            TrashedPhotoEntity(uri = "content://media/1", trashedAt = 1000L),
            TrashedPhotoEntity(uri = "content://media/2", trashedAt = 2000L)
        )
        coEvery { dao.getAllTrashedPhotos() } returns entities

        // Act
        val result = sut.getTrashedPhotos()

        // Assert
        assertThat(result).hasSize(2)
        assertThat(result[0].uri).isEqualTo("content://media/1")
        assertThat(result[1].uri).isEqualTo("content://media/2")
    }

    @Test
    fun `clearAllTrashedPhotos delegates to dao deleteAll`() = runTest {
        // Arrange
        coJustRun { dao.deleteAllTrashedPhotos() }

        // Act
        sut.clearAllTrashedPhotos()

        // Assert
        coVerify(exactly = 1) { dao.deleteAllTrashedPhotos() }
    }

    @Test
    fun `getTrashedPhotosFlow emits mapped domain models from dao`() = runTest {
        // Arrange
        val entities = listOf(TrashedPhotoEntity(uri = "content://media/3", trashedAt = 3000L))
        every { dao.getAllTrashedPhotosFlow() } returns flowOf(entities)

        // Act
        val result = sut.getTrashedPhotosFlow().first()

        // Assert
        assertThat(result).hasSize(1)
        assertThat(result[0].uri).isEqualTo("content://media/3")
    }

    @Test
    fun `getTrashedPhotos returns empty list when dao returns empty`() = runTest {
        // Arrange
        coEvery { dao.getAllTrashedPhotos() } returns emptyList()

        // Act
        val result = sut.getTrashedPhotos()

        // Assert
        assertThat(result).isEmpty()
    }
}
