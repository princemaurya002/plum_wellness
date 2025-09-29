package com.princemaurya.plum_pm.ui.screen.wellnessboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.princemaurya.plum_pm.R
import com.princemaurya.plum_pm.ui.components.Sidebar
import com.princemaurya.plum_pm.ui.viewmodel.WellnessViewModel
import com.princemaurya.plum_pm.ui.viewmodel.LanguageViewModel
// Removed ProvideLocalizedResources to avoid overriding LocalContext which breaks hiltViewModel
import com.princemaurya.plum_pm.ui.util.stringResourceLocalized
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WellnessBoardScreen(
    onNavigateToTipDetail: (String) -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToProfile: () -> Unit,
    isDarkMode: Boolean = false,
    onThemeToggle: () -> Unit = {},
    viewModel: WellnessViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isSidebarOpen by remember { mutableStateOf(false) }

@Composable
fun NoSpinnerPullIndicator(
    isRefreshing: Boolean,
    progress: Float
) {
    // Show only during drag; hide entirely while refreshing (no spinner)
    if (!isRefreshing && progress > 0f) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val rotation = progress * 180f
                    val scale = 0.8f + 0.2f * progress
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(18.dp)
                            .graphicsLayer {
                                rotationZ = rotation
                                scaleX = scale
                                scaleY = scale
                            }
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Pull to refresh",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ElasticPullIndicator(
    isRefreshing: Boolean,
    progress: Float,
    maxHeight: Dp = 72.dp,
    minHeight: Dp = 36.dp
) {
    // Compute height in Dp directly to avoid toPx/toDp conversions
    val heightDp: Dp = minHeight + (maxHeight - minHeight) * progress
    // Bounce scale when transitioning into refreshing
    val bounceScale by animateFloatAsState(
        targetValue = if (isRefreshing) 1.0f else 1.0f + 0.08f * progress,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
        label = "elastic_bounce"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = bounceScale
                    scaleY = bounceScale
                }
        ) {
            // Draw an elastic droplet
            // Capture colors outside the draw scope (composable-only APIs)
            val primaryColor = MaterialTheme.colorScheme.primary
            val primaryFill = primaryColor.copy(alpha = 0.15f)

            Canvas(modifier = Modifier.size(width = 72.dp, height = heightDp)) {
                val w = size.width
                val h = size.height
                val topRadius = (w * 0.22f) * (1f - 0.5f * progress)
                val bottomRadius = (w * 0.28f) * (0.7f + 0.6f * progress)
                val neckY = h * (0.35f + 0.25f * progress)

                val path = Path()
                val cx = w / 2f

                // Start at top center
                path.moveTo(cx, 0f + topRadius)
                // Top circle approximation
                path.addOval(androidx.compose.ui.geometry.Rect(cx - topRadius, 0f, cx + topRadius, topRadius * 2f))

                // Neck to bottom drop
                val leftNeckX = cx - topRadius
                val rightNeckX = cx + topRadius
                val leftBottomX = cx - bottomRadius
                val rightBottomX = cx + bottomRadius
                val bottomY = h

                // Left side curve
                path.moveTo(leftNeckX, neckY)
                path.cubicTo(
                    leftNeckX - topRadius, neckY + (h - neckY) * 0.2f,
                    leftBottomX - bottomRadius * 0.2f, bottomY - (h - neckY) * 0.2f,
                    leftBottomX, bottomY
                )
                // Right side curve
                path.moveTo(rightNeckX, neckY)
                path.cubicTo(
                    rightNeckX + topRadius, neckY + (h - neckY) * 0.2f,
                    rightBottomX + bottomRadius * 0.2f, bottomY - (h - neckY) * 0.2f,
                    rightBottomX, bottomY
                )

                // Bottom arc
                path.addOval(androidx.compose.ui.geometry.Rect(cx - bottomRadius, bottomY - bottomRadius * 2f, cx + bottomRadius, bottomY))

                drawPath(path = path, color = primaryFill)
                drawPath(path = path, color = primaryColor, style = Stroke(width = 2f))
            }

            // Foreground icon/spinner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heightDp),
                contentAlignment = Alignment.Center
            ) {
                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                } else {
                    val rotation = progress * 180f
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(20.dp)
                            .graphicsLayer { rotationZ = rotation }
                    )
                }
            }
        }
    }
}
   // Observe language changes to recompose UI strings without regenerating tips
    val languageViewModel: LanguageViewModel = hiltViewModel()
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val isTranslating by languageViewModel.isTranslating.collectAsState()
    val translationError by languageViewModel.translationError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Auto-generate tips only when screen loads if no tips exist
    LaunchedEffect(Unit) {
        if (uiState.tips.isEmpty() && uiState.userProfile != null) {
            viewModel.generateNewTips()
        }
    }

    // No automatic regeneration on language changes; only when pressing regenerate
    LaunchedEffect(uiState.userProfile) {
        // Intentionally left blank for language changes
    }

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = uiState.isLoading)

    Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = { viewModel.generateNewTips() },
                modifier = Modifier.fillMaxSize(),
                indicator = { state, refreshTriggerDistance ->
                    val density = LocalDensity.current
                    val triggerPx = with(density) { refreshTriggerDistance.toPx() }
                    val progress = if (triggerPx > 0f) (state.indicatorOffset / triggerPx).coerceIn(0f, 1f) else 0f
                    NoSpinnerPullIndicator(isRefreshing = state.isRefreshing, progress = progress)
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                // Snackbar for translation errors
                SnackbarHost(hostState = snackbarHostState)
                LaunchedEffect(translationError) {
                    translationError?.let {
                        snackbarHostState.showSnackbar(it)
                        languageViewModel.clearTranslationError()
                    }
                }

                // Top translating indicator
                AnimatedVisibility(visible = isTranslating) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Top App Bar
                TopAppBar(
                    title = {
                        Crossfade(targetState = currentLanguage, label = "title_lang") { _ ->
                            Text(
                                text = stringResourceLocalized(R.string.wellness_board_title, currentLanguage) + " 🌟",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToFavorites) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = stringResourceLocalized(R.string.favorites, currentLanguage),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { isSidebarOpen = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResourceLocalized(R.string.open_menu, currentLanguage)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )

            // Content
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
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
                        // Animated loading phrases that feel natural and non-repetitive
                        val phrases = remember {
                            listOf(
                                "Planning next steps…",
                                "Thinking it through…",
                                "Gathering ideas…",
                                "Searching insights…",
                                "Connecting the dots…",
                                "Checking your profile…",
                                "Refining suggestions…",
                                "Calibrating balance…",
                                "Adding a pinch of calm…",
                                "Almost there…"
                            )
                        }
                        val cycle by remember(uiState.isLoading) { mutableStateOf(phrases.shuffled()) }
                        var phraseIndex by remember { mutableStateOf(0) }
                        LaunchedEffect(uiState.isLoading) {
                            if (uiState.isLoading) {
                                var i = 0
                                while (uiState.isLoading) {
                                    phraseIndex = i % cycle.size
                                    i++
                                    // Increased interval for a calmer rotation
                                    delay(1600L)
                                }
                            }
                        }
                        Crossfade(targetState = phraseIndex, label = "loading_phrase") { i ->
                            Text(
                                text = cycle[i],
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else if (uiState.tips.isEmpty()) {
                // Empty State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Text(
                            text = "🌱",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Text(
                            text = stringResourceLocalized(R.string.empty_state_title, currentLanguage),
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResourceLocalized(R.string.empty_state_subtitle, currentLanguage),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                        Button(
                            onClick = { viewModel.generateNewTips() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResourceLocalized(R.string.generate_tips_button, currentLanguage))
                        }
                    }
                }
            } else {
                // Tips List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = uiState.tips,
                        key = { it.id }
                    ) { tip ->
                        WellnessTipCard(
                            tip = tip,
                            currentLanguage = currentLanguage,
                            onTipClick = { onNavigateToTipDetail(tip.id) },
                            onFavoriteClick = { viewModel.toggleFavorite(tip) }
                        )
                    }
                }
            }
            // Close Column and SwipeRefresh
            }

            // Sidebar overlay
            Sidebar(
                isOpen = isSidebarOpen,
                onClose = { isSidebarOpen = false },
                onDarkModeToggle = onThemeToggle,
                onEditProfile = onNavigateToProfile,
                onRegenerateTips = {
                    isSidebarOpen = false
                    viewModel.generateNewTips()
                },
                isDarkMode = isDarkMode,
                languageViewModel = languageViewModel
            )
        }
    }
}

@Composable
fun WellnessTipCard(
    tip: com.princemaurya.plum_pm.data.model.WellnessTip,
    currentLanguage: String,
    onTipClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTipClick() }
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = tip.icon, style = MaterialTheme.typography.headlineMedium)
                    Column {
                        Text(
                            text = tip.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = tip.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                IconButton(onClick = onFavoriteClick) {
                    val targetTint = if (tip.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    val animatedTint by animateColorAsState(targetValue = targetTint, label = "fav_tint")
                    val animatedScale by animateFloatAsState(targetValue = if (tip.isFavorite) 1.1f else 1f, animationSpec = spring(), label = "fav_scale")
                    Icon(
                        imageVector = if (tip.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = animatedTint,
                        modifier = Modifier.graphicsLayer(scaleX = animatedScale, scaleY = animatedScale)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = tip.summary,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResourceLocalized(R.string.tap_to_learn_more, currentLanguage),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
