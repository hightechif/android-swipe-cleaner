package com.hightechif.swipecleaner.ui.feature.kept

import com.google.common.truth.Truth.assertThat
import com.hightechif.swipecleaner.domain.model.KeptPhoto
import com.hightechif.swipecleaner.domain.model.MediaImage
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class KeptPhotoMapperTest {

    @Test
    fun `toResolved maps bucket info from matching media image`() {
        // Arrange
        val kept = listOf(KeptPhoto(uri = "uri1", keptAt = 10L))
        val media = listOf(MediaImage(uri = "uri1", bucketId = "b1", bucketName = "Camera"))

        // Act
        val result = kept.toResolved(media)

        // Assert
        assertThat(result).containsExactly(ResolvedKeptPhoto("uri1", 10L, "b1", "Camera"))
    }

    @Test
    fun `toResolved falls back to Others bucket when media image is missing`() {
        // Arrange
        val kept = listOf(KeptPhoto(uri = "missing", keptAt = 5L))

        // Act
        val result = kept.toResolved(emptyList())

        // Assert
        assertThat(result.single().bucketId).isEqualTo("unknown")
        assertThat(result.single().bucketName).isEqualTo("Others")
    }

    @Test
    fun `toKeptAlbums groups by bucket and sorts by name`() {
        // Arrange
        val photos = listOf(
            ResolvedKeptPhoto("a1", 1L, "b2", "Zebra"),
            ResolvedKeptPhoto("a2", 2L, "b1", "Camera"),
            ResolvedKeptPhoto("a3", 3L, "b2", "Zebra")
        )

        // Act
        val result = photos.toKeptAlbums()

        // Assert
        assertThat(result.map { it.name }).containsExactly("Camera", "Zebra").inOrder()
        val zebra = result.last()
        assertThat(zebra.photoCount).isEqualTo(2)
        assertThat(zebra.coverPhotoUri).isEqualTo("a1")
    }

    @Test
    fun `toKeptAlbums returns empty list for empty input`() {
        // Arrange / Act
        val result = emptyList<ResolvedKeptPhoto>().toKeptAlbums()

        // Assert
        assertThat(result).isEmpty()
    }
}
