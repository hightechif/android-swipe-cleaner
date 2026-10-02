package com.hightechif.swipecleaner.data.repository

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.provider.MediaStore
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.hightechif.swipecleaner.domain.model.Album
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowContentResolver

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = Application::class)
class MediaStoreRepositoryTest {

    private lateinit var provider: FakeMediaProvider
    private lateinit var sut: MediaStoreRepository

    @Before
    fun setUp() {
        provider = Robolectric.setupContentProvider(FakeMediaProvider::class.java, MediaStore.AUTHORITY)
        ShadowContentResolver.registerProviderInternal(MediaStore.AUTHORITY, provider)
        sut = MediaStoreRepository(ApplicationProvider.getApplicationContext())
    }

    @After
    fun tearDown() {
        provider.rows = emptyList()
        provider.failOnQuery = false
    }

    @Test
    fun `queryAllImageUris returns uri for every row`() {
        // Arrange
        provider.rows = listOf(row(1, "b1", "Camera"), row(2, "b2", "Download"))

        // Act
        val result = sut.queryAllImageUris()

        // Assert
        assertThat(result).hasSize(2)
        assertThat(result[0]).endsWith("/1")
        assertThat(result[1]).endsWith("/2")
    }

    @Test
    fun `queryAllImageUris returns empty list when no images`() {
        // Arrange
        provider.rows = emptyList()

        // Act
        val result = sut.queryAllImageUris()

        // Assert
        assertThat(result).isEmpty()
    }

    @Test
    fun `queryAllImageUris returns empty list when query throws`() {
        // Arrange
        provider.failOnQuery = true

        // Act
        val result = sut.queryAllImageUris()

        // Assert
        assertThat(result).isEmpty()
    }

    @Test
    fun `queryImageUrisFromBucket filters by bucket id selection`() {
        // Arrange
        provider.rows = listOf(row(1, "b1", "Camera"))

        // Act
        sut.queryImageUrisFromBucket("b1")

        // Assert
        assertThat(provider.lastSelection).isEqualTo("${MediaStore.Images.Media.BUCKET_ID} = ?")
        assertThat(provider.lastSelectionArgs).asList().containsExactly("b1")
    }

    @Test
    fun `queryAllMediaImages maps rows and skips rows without bucket id`() {
        // Arrange
        provider.rows = listOf(row(1, "b1", "Camera"), row(2, null, "Orphan"))

        // Act
        val result = sut.queryAllMediaImages()

        // Assert
        assertThat(result).hasSize(1)
        assertThat(result.single().bucketId).isEqualTo("b1")
        assertThat(result.single().bucketName).isEqualTo("Camera")
    }

    @Test
    fun `queryAllMediaImages uses Unknown name when bucket name is null`() {
        // Arrange
        provider.rows = listOf(row(1, "b1", null))

        // Act
        val result = sut.queryAllMediaImages()

        // Assert
        assertThat(result.single().bucketName).isEqualTo("Unknown")
    }

    @Test
    fun `queryAllAlbums groups by bucket counts photos and sorts by name`() {
        // Arrange
        provider.rows = listOf(
            row(1, "b2", "Zebra"),
            row(2, "b1", "Camera"),
            row(3, "b2", "Zebra")
        )

        // Act
        val result = sut.queryAllAlbums()

        // Assert
        assertThat(result.map(Album::name)).containsExactly("Camera", "Zebra").inOrder()
        val zebra = result.last()
        assertThat(zebra.photoCount).isEqualTo(2)
        assertThat(zebra.coverPhotoUri).endsWith("/1")
    }

    @Test
    fun `queryAllAlbums returns empty list when no images`() {
        // Arrange
        provider.rows = emptyList()

        // Act
        val result = sut.queryAllAlbums()

        // Assert
        assertThat(result).isEmpty()
    }

    @Test
    fun `deleteUrisLegacy returns false on Android R and above`() {
        // Arrange / Act
        val result = sut.deleteUrisLegacy(listOf("content://media/external/images/media/1"))

        // Assert
        assertThat(result).isFalse()
    }

    @Test
    @Config(sdk = [28])
    fun `deleteUrisLegacy returns true when every delete succeeds below Android R`() {
        // Arrange
        provider.deleteCount = 1

        // Act
        val result = sut.deleteUrisLegacy(listOf("content://media/external/images/media/1"))

        // Assert
        assertThat(result).isTrue()
    }

    @Test
    @Config(sdk = [28])
    fun `deleteUrisLegacy returns false when a delete affects no rows below Android R`() {
        // Arrange
        provider.deleteCount = 0

        // Act
        val result = sut.deleteUrisLegacy(listOf("content://media/external/images/media/1"))

        // Assert
        assertThat(result).isFalse()
    }

    @Test
    @Config(sdk = [28])
    fun `createTrashRequest returns null handle below Android R`() {
        // Arrange / Act
        val result = sut.createTrashRequest(listOf("content://media/external/images/media/1"))

        // Assert
        assertThat(result.handle).isNull()
    }

    private fun row(id: Long, bucketId: String?, bucketName: String?) = arrayOf<Any?>(id, bucketId, bucketName)

    class FakeMediaProvider : ContentProvider() {
        var rows: List<Array<Any?>> = emptyList()
        var failOnQuery = false
        var deleteCount = 1
        var lastSelection: String? = null
        var lastSelectionArgs: Array<String>? = null

        override fun onCreate() = true

        override fun query(
            uri: Uri,
            projection: Array<String>?,
            selection: String?,
            selectionArgs: Array<String>?,
            sortOrder: String?
        ): Cursor {
            if (failOnQuery) throw IllegalStateException("query failed")
            lastSelection = selection
            lastSelectionArgs = selectionArgs
            val cursor = MatrixCursor(
                arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME
                )
            )
            rows.forEach { cursor.addRow(it) }
            return cursor
        }

        override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?) = deleteCount
        override fun getType(uri: Uri): String? = null
        override fun insert(uri: Uri, values: ContentValues?): Uri? = null
        override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?) = 0
    }
}
