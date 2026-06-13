package com.hightechif.swipecleaner.data.source.local

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
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
    private lateinit var sut: TrashedPhotoDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        sut = database.trashedPhotoDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `insertEntity then getAll returns matching entity with all fields intact`() = runTest {
        // Arrange
        val entity = TrashedPhotoEntity(uri = "content://media/1", trashedAt = 1000L)

        // Act
        sut.insertTrashedPhoto(entity)
        val result = sut.getAllTrashedPhotos()

        // Assert
        assertThat(result).hasSize(1)
        assertThat(result[0].uri).isEqualTo("content://media/1")
        assertThat(result[0].trashedAt).isEqualTo(1000L)
    }

    @Test
    fun `deleteEntity removes it from the table`() = runTest {
        // Arrange
        val entity = TrashedPhotoEntity(uri = "content://media/2", trashedAt = 2000L)
        sut.insertTrashedPhoto(entity)

        // Act
        sut.deleteTrashedPhoto("content://media/2")
        val result = sut.getAllTrashedPhotos()

        // Assert
        assertThat(result).isEmpty()
    }

    @Test
    fun `getAll returns empty list when table is empty`() = runTest {
        // Act
        val result = sut.getAllTrashedPhotos()

        // Assert
        assertThat(result).isEmpty()
    }

    @Test
    fun `getAll returns all inserted entities`() = runTest {
        // Arrange
        val entity1 = TrashedPhotoEntity(uri = "content://media/3", trashedAt = 3000L)
        val entity2 = TrashedPhotoEntity(uri = "content://media/4", trashedAt = 4000L)
        sut.insertTrashedPhoto(entity1)
        sut.insertTrashedPhoto(entity2)

        // Act
        val result = sut.getAllTrashedPhotos()

        // Assert
        assertThat(result).hasSize(2)
        assertThat(result.map { it.uri }).containsExactly("content://media/3", "content://media/4")
    }

    @Test
    fun `getAllTrashedPhotosFlow emits current table contents`() = runTest {
        // Arrange
        val entity = TrashedPhotoEntity(uri = "content://media/5", trashedAt = 5000L)
        sut.insertTrashedPhoto(entity)

        // Act
        val result = sut.getAllTrashedPhotosFlow().first()

        // Assert
        assertThat(result).hasSize(1)
        assertThat(result[0].uri).isEqualTo("content://media/5")
    }
}
