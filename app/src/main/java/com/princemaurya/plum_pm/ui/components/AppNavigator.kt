package com.princemaurya.plum_pm.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.princemaurya.plum_pm.navigation.Screen
import com.princemaurya.plum_pm.navigation.WellnessNavigation
import com.princemaurya.plum_pm.ui.viewmodel.StartupViewModel

@Composable
fun AppNavigator(
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit,
    modifier: Modifier = Modifier,
    startupViewModel: StartupViewModel = hiltViewModel()
) {
    val uiState by startupViewModel.uiState.collectAsState()
    val navController = rememberNavController()
    
    if (uiState.isLoading) {
        // Show loading screen while checking profile
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Loading your wellness journey...",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    } else {
        // Navigate to appropriate screen based on profile existence
        val startDestination = if (uiState.hasProfile) {
            Screen.WellnessBoard.route
        } else {
            Screen.Profile.route
        }
        
        WellnessNavigation(
            navController = navController,
            startDestination = startDestination,
            isDarkMode = isDarkMode,
            onThemeToggle = onThemeToggle
        )
    }
}
