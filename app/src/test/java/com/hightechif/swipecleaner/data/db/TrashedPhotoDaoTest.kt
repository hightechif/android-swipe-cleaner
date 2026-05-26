package com.hightechif.swipecleaner.data.db

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = Application::class)
class TrashedPhotoDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: TrashedPhotoDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.trashedPhotoDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    // Task 2.2: Insert entity then retrieve it with all fields intact
    @Test
    fun insertEntity_thenGetAll_returnsMatchingEntity() = runTest {
        val entity = TrashedPhotoEntity(uri = "content://media/1", trashedAt = 1000L)

        dao.insertTrashedPhoto(entity)

        val result = dao.getAllTrashedPhotos()
        assertEquals(1, result.size)
        assertEquals("content://media/1", result[0].uri)
        assertEquals(1000L, result[0].trashedAt)
    }

    // Task 2.3: Delete entity by URI removes it
    @Test
    fun deleteEntity_removesItFromTable() = runTest {
        val entity = TrashedPhotoEntity(uri = "content://media/2", trashedAt = 2000L)
        dao.insertTrashedPhoto(entity)

        dao.deleteTrashedPhoto("content://media/2")

        val result = dao.getAllTrashedPhotos()
        assertTrue("Table should be empty after deletion", result.isEmpty())
    }

    // Task 2.4: getAll() returns empty list when table is empty
    @Test
    fun getAll_returnsEmptyList_whenTableIsEmpty() = runTest {
        val result = dao.getAllTrashedPhotos()
        assertTrue("Expected empty list", result.isEmpty())
    }

    // Task 2.5: getAll() returns all inserted entities
    @Test
    fun getAll_returnsAllInsertedEntities() = runTest {
        val entity1 = TrashedPhotoEntity(uri = "content://media/3", trashedAt = 3000L)
        val entity2 = TrashedPhotoEntity(uri = "content://media/4", trashedAt = 4000L)
        dao.insertTrashedPhoto(entity1)
        dao.insertTrashedPhoto(entity2)

        val result = dao.getAllTrashedPhotos()

        assertEquals(2, result.size)
        val uris = result.map { it.uri }
        assertTrue(uris.contains("content://media/3"))
        assertTrue(uris.contains("content://media/4"))
    }

    // Flow variant: getAllTrashedPhotosFlow emits updated list
    @Test
    fun getAllTrashedPhotosFlow_emitsCurrentList() = runTest {
        val entity = TrashedPhotoEntity(uri = "content://media/5", trashedAt = 5000L)
        dao.insertTrashedPhoto(entity)

        val result = dao.getAllTrashedPhotosFlow().first()

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("content://media/5", result[0].uri)
    }
}
