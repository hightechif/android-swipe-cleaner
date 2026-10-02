package com.hightechif.swipecleaner.ui.component

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
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
class EmptySwipeViewCompTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun trashButtonIsDisabledAndLabelledWhenQueueIsEmpty() {
        // Arrange / Act
        composeRule.setContent { EmptySwipeViewComp(deleteQueueSize = 0, onExecuteTrash = {}, onSeeKept = {}) }

        // Assert
        composeRule.onNodeWithText(context.getString(R.string.empty_no_photos_to_delete)).assertIsNotEnabled()
    }

    @Test
    fun trashButtonIsEnabledAndInvokesCallbackWhenQueueHasPhotos() {
        // Arrange
        var executed = 0
        composeRule.setContent { EmptySwipeViewComp(5, onExecuteTrash = { executed++ }, onSeeKept = {}) }
        val label = context.getString(R.string.trash_move_photos, 5)

        // Act
        composeRule.onNodeWithText(label).assertIsEnabled().performClick()

        // Assert
        assertThat(executed).isEqualTo(1)
    }

    @Test
    fun seeKeptButtonInvokesCallback() {
        // Arrange
        var opened = 0
        composeRule.setContent { EmptySwipeViewComp(0, onExecuteTrash = {}, onSeeKept = { opened++ }) }

        // Act
        composeRule.onNodeWithText(context.getString(R.string.action_see_kept)).performClick()

        // Assert
        assertThat(opened).isEqualTo(1)
    }
}
