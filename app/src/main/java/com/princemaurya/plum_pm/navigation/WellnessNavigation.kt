package com.princemaurya.plum_pm.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.princemaurya.plum_pm.ui.screen.favorites.FavoritesScreen
import com.princemaurya.plum_pm.ui.screen.profile.ProfileScreen
import com.princemaurya.plum_pm.ui.screen.tipdetail.TipDetailScreen
import com.princemaurya.plum_pm.ui.screen.wellnessboard.WellnessBoardScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WellnessNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Profile.route,
    isDarkMode: Boolean = false,
    onThemeToggle: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            route = Screen.Profile.route,
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(300)
                )
            }
        ) {
            ProfileScreen(
                onNavigateToWellnessBoard = {
                    navController.navigate(Screen.WellnessBoard.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                },
                isDarkMode = isDarkMode,
                onThemeToggle = onThemeToggle
            )
        }
        
        composable(
            route = Screen.WellnessBoard.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(300)
                )
            }
        ) {
            WellnessBoardScreen(
                onNavigateToTipDetail = { tipId ->
                    navController.navigate("${Screen.TipDetail.route}/$tipId")
                },
                onNavigateToFavorites = {
                    navController.navigate(Screen.Favorites.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                isDarkMode = isDarkMode,
                onThemeToggle = onThemeToggle
            )
        }
        
        composable(
            route = "${Screen.TipDetail.route}/{tipId}",
            arguments = listOf(),
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(300)
                )
            }
        ) { backStackEntry ->
            val tipId = backStackEntry.arguments?.getString("tipId") ?: ""
            TipDetailScreen(
                tipId = tipId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.Favorites.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                )
            }
        ) {
            FavoritesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToTipDetail = { tipId ->
                    navController.navigate("${Screen.TipDetail.route}/$tipId")
                }
            )
        }
    }
}
