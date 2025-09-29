package com.princemaurya.plum_pm.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.activity.ComponentActivity
import androidx.compose.ui.zIndex
import com.princemaurya.plum_pm.R
import com.princemaurya.plum_pm.ui.viewmodel.LanguageViewModel
import com.princemaurya.plum_pm.ui.util.stringResourceLocalized

@Composable
private fun SidebarMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isPressed = true },
                    onDragEnd = { isPressed = false }
                ) { _, _ -> }
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isPressed)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isPressed) 2.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Sidebar(
    isOpen: Boolean,
    onClose: () -> Unit,
    onDarkModeToggle: () -> Unit,
    onEditProfile: () -> Unit,
    onRegenerateTips: () -> Unit,
    isDarkMode: Boolean = false,
    modifier: Modifier = Modifier,
    languageViewModel: LanguageViewModel // must be passed explicitly
) {
    var showLanguageDialog by remember { mutableStateOf(false) }
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val context = LocalContext.current
    Box(modifier = modifier.fillMaxSize()) {

        // Backdrop overlay
        AnimatedVisibility(
            visible = isOpen,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { onClose() }
                    .zIndex(1f)
            )
        }

        // Sidebar content
        AnimatedVisibility(
            visible = isOpen,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ),
            modifier = Modifier
                .zIndex(2f)
                .align(Alignment.CenterEnd)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(280.dp)
                    .padding(top = 16.dp, bottom = 16.dp, end = 16.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { _, dragAmount ->
                            if (dragAmount.x > 50) onClose()
                        }
                    },
                shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {

                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResourceLocalized(R.string.menu, currentLanguage),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = onClose) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResourceLocalized(R.string.close_menu, currentLanguage),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Divider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Menu Items
                    SidebarMenuItem(
                        icon = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        title = stringResourceLocalized(if (isDarkMode) R.string.light_mode else R.string.dark_mode, currentLanguage),
                        subtitle = stringResourceLocalized(R.string.toggle_theme, currentLanguage),
                        onClick = {
                            onClose()
                            onDarkModeToggle()
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SidebarMenuItem(
                        icon = Icons.Default.Person,
                        title = stringResourceLocalized(R.string.edit_profile, currentLanguage),
                        subtitle = stringResourceLocalized(R.string.update_your_information, currentLanguage),
                        onClick = {
                            onClose()
                            onEditProfile()
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SidebarMenuItem(
                        icon = Icons.Default.Refresh,
                        title = stringResourceLocalized(R.string.regenerate_tips, currentLanguage),
                        subtitle = stringResourceLocalized(R.string.get_new_wellness_tips, currentLanguage),
                        onClick = {
                            onClose()
                            onRegenerateTips()
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SidebarMenuItem(
                        icon = Icons.Default.Language,
                        title = stringResourceLocalized(R.string.language, currentLanguage),
                        subtitle = languageViewModel.getCurrentLanguageDisplayName(),
                        onClick = {
                            onClose()
                            showLanguageDialog = true
                        }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Footer
                    Text(
                        text = stringResourceLocalized(R.string.app_name, currentLanguage) + " 🌟",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        // Language Picker Dialog
        LanguagePickerDialog(
            isVisible = showLanguageDialog,
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { language ->
                languageViewModel.setLanguage(language.code, context as? ComponentActivity)
            },
            currentLanguageCode = currentLanguage
        )
    }
}
