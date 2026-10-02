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
class MilestoneDialogCompTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun emptyTrashButtonIsHiddenWhenTrashIsEmpty() {
        // Arrange / Act
        composeRule.setContent {
            MilestoneDialogComp(swipeCount = 50, trashCount = 0, onReviewTrash = {}, onEmptyTrash = {}, onDismiss = {})
        }

        // Assert
        composeRule.onNodeWithText(context.getString(R.string.action_empty_trash)).assertDoesNotExist()
    }

    @Test
    fun emptyTrashButtonIsShownAndClickableWhenTrashHasPhotos() {
        // Arrange
        var emptied = 0
        composeRule.setContent {
            MilestoneDialogComp(50, 3, onReviewTrash = {}, onEmptyTrash = { emptied++ }, onDismiss = {})
        }

        // Act
        composeRule.onNodeWithText(context.getString(R.string.action_empty_trash)).performClick()

        // Assert
        assertThat(emptied).isEqualTo(1)
    }

    @Test
    fun reviewTrashButtonShowsTrashCountAndInvokesCallback() {
        // Arrange
        var reviewed = 0
        composeRule.setContent {
            MilestoneDialogComp(50, 3, onReviewTrash = { reviewed++ }, onEmptyTrash = {}, onDismiss = {})
        }

        // Act
        composeRule.onNodeWithText(context.getString(R.string.milestone_review_trash, 3)).performClick()

        // Assert
        assertThat(reviewed).isEqualTo(1)
    }

    @Test
    fun keepSwipingInvokesOndismiss() {
        // Arrange
        var dismissed = 0
        composeRule.setContent {
            MilestoneDialogComp(50, 0, onReviewTrash = {}, onEmptyTrash = {}, onDismiss = { dismissed++ })
        }

        // Act
        composeRule.onNodeWithText(context.getString(R.string.action_keep_swiping)).performClick()

        // Assert
        assertThat(dismissed).isEqualTo(1)
    }
}
