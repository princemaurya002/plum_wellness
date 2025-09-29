package com.princemaurya.plum_pm.navigation

sealed class Screen(val route: String) {
    object Profile : Screen("profile")
    object WellnessBoard : Screen("wellness_board")
    object TipDetail : Screen("tip_detail")
    object Favorites : Screen("favorites")
}
