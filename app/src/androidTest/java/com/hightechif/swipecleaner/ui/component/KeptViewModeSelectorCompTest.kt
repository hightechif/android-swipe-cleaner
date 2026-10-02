package com.hightechif.swipecleaner.ui.component

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
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
class KeptViewModeSelectorCompTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun selectorShowsLabelOfCurrentMode() {
        // Arrange / Act
        composeRule.setContent { KeptViewModeSelectorComp(KeptViewMode.ALBUMS, onViewModeSelected = {}) }

        // Assert
        composeRule.onNodeWithText(context.getString(R.string.kept_view_albums)).assertExists()
    }

    @Test
    fun choosingAMenuItemReportsTheSelectedMode() {
        // Arrange
        var selected: KeptViewMode? = null
        composeRule.setContent { KeptViewModeSelectorComp(KeptViewMode.ALL_PHOTOS, { selected = it }) }
        composeRule.onNodeWithText(context.getString(R.string.kept_view_all)).performClick()

        // Act
        composeRule.onAllNodesWithText(context.getString(R.string.kept_view_albums)).onFirst().performClick()

        // Assert
        assertThat(selected).isEqualTo(KeptViewMode.ALBUMS)
    }
}
