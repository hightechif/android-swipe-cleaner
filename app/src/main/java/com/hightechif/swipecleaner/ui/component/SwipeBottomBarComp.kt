package com.hightechif.swipecleaner.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hightechif.swipecleaner.R
import com.hightechif.swipecleaner.ui.feature.swipe.SwipeTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeBottomBarComp(
    activeTab: SwipeTab,
    keptCount: Int,
    trashCount: Int,
    onTabSelected: (SwipeTab) -> Unit
) {
    NavigationBar(containerColor = Color(0xFF1E1E2C), tonalElevation = 8.dp) {
        NavigationBarItem(
            selected = activeTab == SwipeTab.SWIPE,
            onClick = { onTabSelected(SwipeTab.SWIPE) },
            icon = {
                Icon(Icons.Default.Collections, contentDescription = stringResource(R.string.swipe_tab_swipe))
            },
            label = { Text(stringResource(R.string.swipe_tab_swipe)) },
            colors = tabColors(Color(0xFF6C63FF))
        )
        NavigationBarItem(
            selected = activeTab == SwipeTab.KEPT,
            onClick = { onTabSelected(SwipeTab.KEPT) },
            icon = {
                BadgedBox(badge = {
                    if (keptCount > 0) {
                        Badge(containerColor = Color(0xFF4CAF50)) {
                            Text(text = keptCount.toString(), color = Color.White)
                        }
                    }
                }) {
                    Icon(Icons.Default.Favorite, contentDescription = stringResource(R.string.swipe_tab_kept))
                }
            },
            label = { Text(stringResource(R.string.swipe_tab_kept)) },
            colors = tabColors(Color(0xFF4CAF50))
        )
        NavigationBarItem(
            selected = activeTab == SwipeTab.TRASH,
            onClick = { onTabSelected(SwipeTab.TRASH) },
            icon = {
                BadgedBox(badge = {
                    if (trashCount > 0) {
                        Badge(containerColor = Color(0xFFE91E63)) {
                            Text(text = trashCount.toString(), color = Color.White)
                        }
                    }
                }) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.swipe_tab_trash))
                }
            },
            label = { Text(stringResource(R.string.swipe_tab_trash)) },
            colors = tabColors(Color(0xFFE91E63))
        )
    }
}

@Composable
private fun tabColors(selected: Color) = NavigationBarItemDefaults.colors(
    selectedIconColor = selected,
    selectedTextColor = selected,
    unselectedIconColor = Color.Gray,
    unselectedTextColor = Color.Gray,
    indicatorColor = Color(0xFF252538)
)
