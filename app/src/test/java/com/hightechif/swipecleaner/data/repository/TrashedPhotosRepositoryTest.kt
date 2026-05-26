package com.hightechif.swipecleaner.data.repository

import com.hightechif.swipecleaner.data.db.TrashedPhotoDao
import com.hightechif.swipecleaner.data.db.TrashedPhotoEntity
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class TrashedPhotosRepositoryTest {

    private lateinit var dao: TrashedPhotoDao
    private lateinit var repository: TrashedPhotosRepository

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        repository = TrashedPhotosRepositoryImpl(dao)
    }

    // Task 3.2: addToTrash (insertTrashedPhoto) calls DAO insert with correct entity
    @Test
    fun insertTrashedPhoto_callsDaoInsertWithCorrectUri() = runTest {
        val slot = slot<TrashedPhotoEntity>()
        coJustRun { dao.insertTrashedPhoto(capture(slot)) }

        repository.insertTrashedPhoto("content://media/test/1")

        coVerify(exactly = 1) { dao.insertTrashedPhoto(any()) }
        assertEquals("content://media/test/1", slot.captured.uri)
    }

    // Task 3.3: deleteTrashedPhoto calls DAO delete with the given URI
    @Test
    fun deleteTrashedPhoto_callsDaoDeleteWithCorrectUri() = runTest {
        val uriSlot = slot<String>()
        coJustRun { dao.deleteTrashedPhoto(capture(uriSlot)) }

        repository.deleteTrashedPhoto("content://media/test/2")

        coVerify(exactly = 1) { dao.deleteTrashedPhoto("content://media/test/2") }
        assertEquals("content://media/test/2", uriSlot.captured)
    }

    // Task 3.4: getTrashedPhotos maps all DAO entities
    @Test
    fun getTrashedPhotos_returnsAllEntitiesFromDao() = runTest {
        val entities = listOf(
            TrashedPhotoEntity(uri = "content://media/1", trashedAt = 1000L),
            TrashedPhotoEntity(uri = "content://media/2", trashedAt = 2000L)
        )
        coEvery { dao.getAllTrashedPhotos() } returns entities

        val result = repository.getTrashedPhotos()

        assertEquals(2, result.size)
        assertEquals("content://media/1", result[0].uri)
        assertEquals("content://media/2", result[1].uri)
    }

    // Extra: clearAllTrashedPhotos delegates to DAO
    @Test
    fun clearAllTrashedPhotos_callsDaoDeleteAll() = runTest {
        coJustRun { dao.deleteAllTrashedPhotos() }

        repository.clearAllTrashedPhotos()

        coVerify(exactly = 1) { dao.deleteAllTrashedPhotos() }
    }

    // Extra: getTrashedPhotosFlow delegates to DAO flow
    @Test
    fun getTrashedPhotosFlow_emitsDaoFlowValues() = runTest {
        val entities = listOf(TrashedPhotoEntity(uri = "content://media/3", trashedAt = 3000L))
        coEvery { dao.getAllTrashedPhotosFlow() } returns flowOf(entities)

        val flow = repository.getTrashedPhotosFlow()
        var result: List<TrashedPhotoEntity>? = null
        flow.collect { result = it }

        assertEquals(1, result?.size)
        assertEquals("content://media/3", result?.get(0)?.uri)
    }

    // Extra: getTrashedPhotos returns empty when DAO returns empty
    @Test
    fun getTrashedPhotos_returnsEmptyList_whenDaoReturnsEmpty() = runTest {
        coEvery { dao.getAllTrashedPhotos() } returns emptyList()

        val result = repository.getTrashedPhotos()

        assertTrue(result.isEmpty())
    }
}
