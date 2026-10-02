package com.hightechif.swipecleaner.ui.component

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.hightechif.swipecleaner.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TrashTabContentCompTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun emptyStateIsShownWhenQueueIsEmpty() {
        // Arrange / Act
        composeRule.setContent { TrashTabContentComp(emptyList(), onRestorePhoto = {}, onExecuteTrash = {}) }

        // Assert
        composeRule.onNodeWithText(context.getString(R.string.trash_empty)).assertExists()
    }

    @Test
    fun moveButtonShowsQueueSizeAndInvokesCallback() {
        // Arrange
        var executed = 0
        composeRule.setContent {
            TrashTabContentComp(listOf("a", "b", "c"), onRestorePhoto = {}, onExecuteTrash = { executed++ })
        }

        // Act
        composeRule.onNodeWithText(context.getString(R.string.trash_move_photos, 3)).performClick()

        // Assert
        assertThat(executed).isEqualTo(1)
    }

    @Test
    fun moveButtonIsAbsentWhenQueueIsEmpty() {
        // Arrange / Act
        composeRule.setContent { TrashTabContentComp(emptyList(), onRestorePhoto = {}, onExecuteTrash = {}) }

        // Assert
        composeRule.onNodeWithText(context.getString(R.string.trash_move_photos, 0)).assertDoesNotExist()
    }
}
