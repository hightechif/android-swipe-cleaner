package com.hightechif.swipecleaner.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hightechif.swipecleaner.ui.feature.completion.CompletionScreen
import com.hightechif.swipecleaner.ui.feature.kept.KeptPhotosScreen
import com.hightechif.swipecleaner.ui.feature.permission.PermissionScreen
import com.hightechif.swipecleaner.ui.feature.swipe.SwipeScreen
import com.hightechif.swipecleaner.ui.feature.swipe.SwipeViewModel
import com.hightechif.swipecleaner.ui.feature.trash.TrashViewModel
import org.koin.androidx.compose.koinViewModel

sealed class Screen(val route: String) {
    data object Permission : Screen("permission")
    data object Swipe : Screen("swipe")
    data object Completion : Screen("completion")
    data object KeptPhotos : Screen("kept_photos")
}

@Composable
fun SwipeCleanerApp(
    navController: NavHostController = rememberNavController()
) {
    val swipeViewModel: SwipeViewModel = koinViewModel()
    val trashViewModel: TrashViewModel = koinViewModel()

    NavHost(navController = navController, startDestination = Screen.Permission.route) {
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
            SwipeScreen(viewModel = swipeViewModel, trashViewModel = trashViewModel)
        }

        composable(Screen.Completion.route) {
            CompletionScreen(
                viewModel = swipeViewModel,
                trashViewModel = trashViewModel,
                onNavigateToKept = { navController.navigate(Screen.KeptPhotos.route) },
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
                onNavigateBack = { navController.popBackStack() },
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
