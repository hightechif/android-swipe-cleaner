package com.hightechif.swipecleaner.data.repository

import com.google.common.truth.Truth.assertThat
import com.hightechif.swipecleaner.domain.model.PendingSystemAction
import com.hightechif.swipecleaner.domain.repository.IMediaStoreRepository
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Contract tests for IMediaStoreRepository.
 *
 * MediaStoreRepository requires ContentResolver and Android system APIs; these tests verify
 * the interface contract via a mock. Full integration tests belong in androidTest.
 */
@RunWith(JUnit4::class)
class MediaStoreRepositoryTest {

    @MockK lateinit var sut: IMediaStoreRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        clearAllMocks()
    }

    @Test
    fun `queryAllImageUris returns list of uris`() {
        // Arrange
        val expectedUris = listOf("content://media/1", "content://media/2")
        every { sut.queryAllImageUris() } returns expectedUris

        // Act
        val result = sut.queryAllImageUris()

        // Assert
        assertThat(result).hasSize(2)
        assertThat(result[0]).isEqualTo("content://media/1")
        assertThat(result[1]).isEqualTo("content://media/2")
    }

    @Test
    fun `queryAllImageUris returns empty list when no images`() {
        // Arrange
        every { sut.queryAllImageUris() } returns emptyList()

        // Act
        val result = sut.queryAllImageUris()

        // Assert
        assertThat(result).isEmpty()
    }

    @Test
    fun `createTrashRequest returns action with null handle when uri list is empty`() {
        // Arrange
        val expected = PendingSystemAction(null)
        every { sut.createTrashRequest(emptyList()) } returns expected

        // Act
        val result = sut.createTrashRequest(emptyList())

        // Assert
        assertThat(result.handle).isNull()
        verify(exactly = 1) { sut.createTrashRequest(emptyList()) }
    }
}
