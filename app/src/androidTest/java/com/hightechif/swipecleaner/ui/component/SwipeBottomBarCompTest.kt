package com.hightechif.swipecleaner.ui.component

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.hightechif.swipecleaner.R
import com.hightechif.swipecleaner.ui.feature.swipe.SwipeTab
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SwipeBottomBarCompTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun clickingEachTabReportsTheMatchingSwipetab() {
        // Arrange
        val selected = mutableListOf<SwipeTab>()
        composeRule.setContent {
            SwipeBottomBarComp(SwipeTab.SWIPE, keptCount = 0, trashCount = 0, onTabSelected = { selected.add(it) })
        }

        // Act
        composeRule.onNodeWithText(context.getString(R.string.swipe_tab_kept)).performClick()
        composeRule.onNodeWithText(context.getString(R.string.swipe_tab_trash)).performClick()
        composeRule.onNodeWithText(context.getString(R.string.swipe_tab_swipe)).performClick()

        // Assert
        assertThat(selected).containsExactly(SwipeTab.KEPT, SwipeTab.TRASH, SwipeTab.SWIPE).inOrder()
    }

    @Test
    fun badgesShowCountsWhenGreaterThanZero() {
        // Arrange / Act
        composeRule.setContent {
            SwipeBottomBarComp(SwipeTab.SWIPE, keptCount = 7, trashCount = 12, onTabSelected = {})
        }

        // Assert
        composeRule.onNodeWithText("7", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithText("12", useUnmergedTree = true).assertExists()
    }

    @Test
    fun badgesAreHiddenWhenCountsAreZero() {
        // Arrange / Act
        composeRule.setContent {
            SwipeBottomBarComp(SwipeTab.SWIPE, keptCount = 0, trashCount = 0, onTabSelected = {})
        }

        // Assert
        composeRule.onNodeWithText("0", useUnmergedTree = true).assertDoesNotExist()
    }
}
