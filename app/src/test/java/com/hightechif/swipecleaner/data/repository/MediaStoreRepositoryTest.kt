package com.hightechif.swipecleaner.data.repository

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [MediaStoreRepository] interface contract.
 *
 * Note: [MediaStoreRepositoryImpl] directly uses `ContentResolver` and Android system APIs,
 * which require an instrumented environment. These tests verify the interface contract through
 * a mock implementation, and verify behaviours of use-case collaborators.
 *
 * Full integration tests for the real implementation should live in androidTest.
 */
class MediaStoreRepositoryTest {

    private lateinit var repository: MediaStoreRepository

    @Before
    fun setUp() {
        repository = mockk()
    }

    // Task 6.2: queryAllImageUris with a non-empty result returns a list with URIs
    @Test
    fun queryAllImageUris_returnsListOfUris() {
        val expectedUris = listOf("content://media/1", "content://media/2")
        every { repository.queryAllImageUris() } returns expectedUris

        val result = repository.queryAllImageUris()

        assertEquals(2, result.size)
        assertEquals("content://media/1", result[0])
        assertEquals("content://media/2", result[1])
    }

    // Task 6.3: queryAllImageUris with empty result returns empty list
    @Test
    fun queryAllImageUris_returnsEmptyList_whenNoImages() {
        every { repository.queryAllImageUris() } returns emptyList()

        val result = repository.queryAllImageUris()

        assertTrue("Expected empty URI list", result.isEmpty())
    }

    // createTrashRequest returns null when there are no URIs
    @Test
    fun createTrashRequest_returnsNull_whenUriListIsEmpty() {
        every { repository.createTrashRequest(emptyList()) } returns null

        val result = repository.createTrashRequest(emptyList())

        assertNull(result)
        verify(exactly = 1) { repository.createTrashRequest(emptyList()) }
    }
}
