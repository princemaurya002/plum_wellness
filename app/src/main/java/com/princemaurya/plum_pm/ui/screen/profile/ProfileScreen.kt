package com.princemaurya.plum_pm.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.princemaurya.plum_pm.R
import com.princemaurya.plum_pm.data.model.*
import com.princemaurya.plum_pm.ui.viewmodel.ProfileViewModel
import com.princemaurya.plum_pm.ui.viewmodel.LanguageViewModel
import com.princemaurya.plum_pm.ui.util.stringResourceLocalized

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToWellnessBoard: () -> Unit,
    isDarkMode: Boolean = false,
    onThemeToggle: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val languageViewModel: LanguageViewModel = hiltViewModel()
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    
    // Track if this is a new save (not just loading existing data)
    var hasJustSaved by remember { mutableStateOf(false) }
    
    // Auto-navigate when profile is newly saved
    LaunchedEffect(uiState.isSaved, uiState.isLoading) {
        if (uiState.isSaved && !uiState.isLoading && hasJustSaved) {
            onNavigateToWellnessBoard()
        }
    }
    
    // Load existing profile data when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadExistingProfile()
    }
    
    // Track when user saves profile
    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) {
            hasJustSaved = true
        }
    }
    
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Theme Toggle Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onThemeToggle) {
                    Icon(
                        if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = if (isDarkMode) stringResourceLocalized(R.string.switch_to_light_mode, currentLanguage) else stringResourceLocalized(R.string.switch_to_dark_mode, currentLanguage),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Header
            Text(
                text = if (uiState.isSaved) stringResourceLocalized(R.string.profile_title_edit, currentLanguage) else stringResourceLocalized(R.string.profile_title_create, currentLanguage),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (uiState.isSaved) stringResourceLocalized(R.string.profile_subtitle_edit, currentLanguage) else stringResourceLocalized(R.string.profile_subtitle_create, currentLanguage),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Main Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Basic Information Section
                    FormSection(stringResourceLocalized(R.string.basic_information, currentLanguage)) {
                        // Name
                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = viewModel::updateName,
                            label = { Text(stringResourceLocalized(R.string.full_name, currentLanguage)) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = uiState.name.isBlank() && uiState.error != null
                    )
                    
                        // Age
                    OutlinedTextField(
                        value = if (uiState.age == 0) "" else uiState.age.toString(),
                        onValueChange = { value ->
                            value.toIntOrNull()?.let { viewModel.updateAge(it) }
                        },
                            label = { Text(stringResourceLocalized(R.string.age, currentLanguage)) },
                        leadingIcon = { Icon(Icons.Default.Cake, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = uiState.age == 0 && uiState.error != null
                    )
                    
                        // Gender
                    Text(
                        text = stringResourceLocalized(R.string.gender, currentLanguage),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Gender.values().forEach { gender ->
                            val isSelected = uiState.gender == gender
                            FilterChip(
                                onClick = { viewModel.updateGender(gender) },
                                label = { Text(gender.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                selected = isSelected,
                                leadingIcon = {
                                    if (isSelected) Icon(Icons.Default.Check, contentDescription = null)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        }
                        
                        // Height and Weight
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = uiState.height?.toString() ?: "",
                                onValueChange = { value ->
                                    value.toFloatOrNull()?.let { viewModel.updateHeight(it) }
                                },
                                label = { Text(stringResourceLocalized(R.string.height_cm, currentLanguage)) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true
                            )
                            
                            OutlinedTextField(
                                value = uiState.weight?.toString() ?: "",
                                onValueChange = { value ->
                                    value.toFloatOrNull()?.let { viewModel.updateWeight(it) }
                                },
                                label = { Text(stringResourceLocalized(R.string.weight_kg, currentLanguage)) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true
                            )
                        }
                    }
                    
                    Divider()
                    
                    // Health Goals Section
                    FormSection(stringResourceLocalized(R.string.health_goals, currentLanguage)) {
                        Text(
                            text = stringResourceLocalized(R.string.primary_goal, currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Column(
                            modifier = Modifier.selectableGroup(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HealthGoal.values().forEach { goal ->
                                val isSelected = uiState.primaryGoal == goal
                                Row(
                                modifier = Modifier
                                        .fillMaxWidth()
                                    .selectable(
                                            selected = isSelected,
                                            onClick = { viewModel.updateHealthGoal(goal) },
                                            role = Role.RadioButton
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = goal.displayName,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                    
                    Divider()
                    
                    // Lifestyle Section
                    FormSection(stringResourceLocalized(R.string.lifestyle, currentLanguage)) {
                        // Activity Level
                        Text(
                            text = stringResourceLocalized(R.string.activity_level, currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Column(
                            modifier = Modifier.selectableGroup(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ActivityLevel.values().forEach { level ->
                                val isSelected = uiState.activityLevel == level
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = isSelected,
                                            onClick = { viewModel.updateActivityLevel(level) },
                                            role = Role.RadioButton
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = level.displayName,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                        
                        // Exercise Preferences
                        Text(
                            text = stringResourceLocalized(R.string.exercise_preferences, currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(ExercisePreference.values().size) { index ->
                                val preference = ExercisePreference.values()[index]
                                val isSelected = uiState.exercisePreferences.contains(preference)
                                FilterChip(
                                    onClick = { viewModel.toggleExercisePreference(preference) },
                                    label = { Text(preference.displayName) },
                                    selected = isSelected,
                                    leadingIcon = {
                                        if (isSelected) Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                )
                            }
                        }
                        
                        // Daily Wellness Time
                        Text(
                            text = stringResourceLocalized(R.string.daily_wellness_time, currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Column(
                            modifier = Modifier.selectableGroup(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DailyWellnessTime.values().forEach { time ->
                                val isSelected = uiState.dailyWellnessTime == time
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = isSelected,
                                            onClick = { viewModel.updateDailyWellnessTime(time) },
                                            role = Role.RadioButton
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = time.displayName,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                    
                    Divider()
                    
                    // Dietary Preferences Section
                    FormSection(stringResourceLocalized(R.string.dietary_preferences, currentLanguage)) {
                        Text(
                            text = stringResourceLocalized(R.string.dietary_preference, currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Column(
                            modifier = Modifier.selectableGroup(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DietaryPreference.values().forEach { preference ->
                                val isSelected = uiState.dietaryPreference == preference
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = isSelected,
                                            onClick = { viewModel.updateDietaryPreference(preference) },
                                            role = Role.RadioButton
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = preference.displayName,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                        
                    Text(
                            text = stringResourceLocalized(R.string.diet_style_optional, currentLanguage),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Column(
                            modifier = Modifier.selectableGroup(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DietStyle.values().forEach { style ->
                                val isSelected = uiState.dietStyle == style
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = isSelected,
                                            onClick = { viewModel.updateDietStyle(style) },
                                            role = Role.RadioButton
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = style.displayName,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                    
                    Divider()
                    
                    // Mental Wellness Section
                    FormSection(stringResourceLocalized(R.string.mental_wellness, currentLanguage)) {
                        Text(
                            text = stringResourceLocalized(R.string.stress_level, currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Column(
                            modifier = Modifier.selectableGroup(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                            StressLevel.values().forEach { level ->
                                val isSelected = uiState.stressLevel == level
                                Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                            selected = isSelected,
                                            onClick = { viewModel.updateStressLevel(level) },
                                            role = Role.RadioButton
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = level.displayName,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                        
                        Text(
                            text = stringResourceLocalized(R.string.mindfulness_experience, currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Column(
                            modifier = Modifier.selectableGroup(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MindfulnessExperience.values().forEach { experience ->
                                val isSelected = uiState.mindfulnessExperience == experience
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = isSelected,
                                            onClick = { viewModel.updateMindfulnessExperience(experience) },
                                            role = Role.RadioButton
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = experience.displayName,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                    
                    Divider()
                    
                    // Work Environment Section
                    FormSection(stringResourceLocalized(R.string.work_style, currentLanguage)) {
                        Text(
                            text = stringResourceLocalized(R.string.work_style, currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Column(
                            modifier = Modifier.selectableGroup(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            WorkStyle.values().forEach { style ->
                                val isSelected = uiState.workStyle == style
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = isSelected,
                                            onClick = { viewModel.updateWorkStyle(style) },
                                            role = Role.RadioButton
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = style.displayName,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                        
                        Text(
                            text = stringResourceLocalized(R.string.screen_time, currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Column(
                            modifier = Modifier.selectableGroup(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ScreenTime.values().forEach { time ->
                                val isSelected = uiState.screenTime == time
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = isSelected,
                                            onClick = { viewModel.updateScreenTime(time) },
                                            role = Role.RadioButton
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = time.displayName,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                    
                    Divider()
                    
                    // Personal Preferences Section
                    FormSection(stringResourceLocalized(R.string.favorite_activities, currentLanguage)) {
                                Text(
                            text = stringResourceLocalized(R.string.favorite_activities, currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(FavoriteActivity.values().size) { index ->
                                val activity = FavoriteActivity.values()[index]
                                val isSelected = uiState.favoriteActivities.contains(activity)
                                FilterChip(
                                    onClick = { viewModel.toggleFavoriteActivity(activity) },
                                    label = { Text(activity.displayName) },
                                    selected = isSelected
                                )
                            }
                        }
                        
                        Text(
                            text = stringResourceLocalized(R.string.motivation_style, currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Column(
                            modifier = Modifier.selectableGroup(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MotivationStyle.values().forEach { style ->
                                val isSelected = uiState.motivationStyle == style
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = isSelected,
                                            onClick = { viewModel.updateMotivationStyle(style) },
                                            role = Role.RadioButton
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = style.displayName,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                        
                        OutlinedTextField(
                            value = uiState.extraInformation,
                            onValueChange = viewModel::updateExtraInformation,
                            label = { Text(stringResourceLocalized(R.string.additional_info, currentLanguage)) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Save Button
            Button(
                onClick = viewModel::saveProfile,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = if (uiState.isSaved) stringResource(id = R.string.edit_profile_button) else stringResource(id = R.string.create_profile_button),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Error Message
            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun FormSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        content()
    }
}