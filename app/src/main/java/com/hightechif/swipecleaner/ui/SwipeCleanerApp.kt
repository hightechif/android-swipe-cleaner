package com.hightechif.swipecleaner.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hightechif.swipecleaner.ui.screen.CompletionScreen
import com.hightechif.swipecleaner.ui.screen.KeptPhotosScreen
import com.hightechif.swipecleaner.ui.screen.PermissionScreen
import com.hightechif.swipecleaner.ui.screen.SwipeScreen
import com.hightechif.swipecleaner.ui.viewmodel.SwipeViewModel
import org.koin.androidx.compose.koinViewModel

sealed class Screen(val route: String) {
    object Permission : Screen("permission")
    object Swipe : Screen("swipe")
    object Completion : Screen("completion")
    object KeptPhotos : Screen("kept_photos")
}

@Composable
fun SwipeCleanerApp(
    navController: NavHostController = rememberNavController()
) {
    // Sharing the SwipeViewModel across screens so we can access the session state
    // and trigger photo pool updates.
    val swipeViewModel: SwipeViewModel = koinViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Permission.route
    ) {
        composable(Screen.Permission.route) {
            PermissionScreen(
                onPermissionGranted = {
                    swipeViewModel.loadPhotoPool()
                    navController.navigate(Screen.Swipe.route) {
                        popUpTo(Screen.Permission.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Swipe.route) {
            SwipeScreen(
                viewModel = swipeViewModel
            )
        }

        composable(Screen.Completion.route) {
            CompletionScreen(
                viewModel = swipeViewModel,
                onNavigateToKept = {
                    navController.navigate(Screen.KeptPhotos.route)
                },
                onNavigateToSwipe = {
                    swipeViewModel.loadPhotoPool()
                    navController.navigate(Screen.Swipe.route) {
                        popUpTo(Screen.Swipe.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.KeptPhotos.route) {
            KeptPhotosScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onResetComplete = {
                    swipeViewModel.loadPhotoPool()
                    navController.navigate(Screen.Swipe.route) {
                        popUpTo(Screen.Swipe.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
