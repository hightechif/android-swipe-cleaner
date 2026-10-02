package com.hightechif.swipecleaner.ui.component

import androidx.compose.ui.graphics.Color
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
class ConfirmDialogCompTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun dialogShowsTitleMessageAndLabels() {
        // Arrange / Act
        composeRule.setContent {
            ConfirmDialogComp("Title", "Message", "Confirm", Color.Red, onConfirm = {}, onDismiss = {})
        }

        // Assert
        composeRule.onNodeWithText("Title").assertExists()
        composeRule.onNodeWithText("Message").assertExists()
        composeRule.onNodeWithText("Confirm").assertExists()
        composeRule.onNodeWithText(context.getString(R.string.action_cancel)).assertExists()
    }

    @Test
    fun confirmClickInvokesOnconfirmOnly() {
        // Arrange
        var confirmed = 0
        var dismissed = 0
        composeRule.setContent {
            ConfirmDialogComp("T", "M", "Confirm", Color.Red, { confirmed++ }, { dismissed++ })
        }

        // Act
        composeRule.onNodeWithText("Confirm").performClick()

        // Assert
        assertThat(confirmed).isEqualTo(1)
        assertThat(dismissed).isEqualTo(0)
    }

    @Test
    fun cancelClickInvokesOndismissOnly() {
        // Arrange
        var confirmed = 0
        var dismissed = 0
        composeRule.setContent {
            ConfirmDialogComp("T", "M", "Confirm", Color.Red, { confirmed++ }, { dismissed++ })
        }

        // Act
        composeRule.onNodeWithText(context.getString(R.string.action_cancel)).performClick()

        // Assert
        assertThat(dismissed).isEqualTo(1)
        assertThat(confirmed).isEqualTo(0)
    }
}
