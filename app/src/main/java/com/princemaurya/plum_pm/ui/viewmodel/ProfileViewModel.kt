package com.princemaurya.plum_pm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.princemaurya.plum_pm.data.model.*
import com.princemaurya.plum_pm.data.repository.WellnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: WellnessRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    init {
        loadExistingProfile()
    }
    
    // Basic Demographics
    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }
    
    fun updateAge(age: Int) {
        _uiState.value = _uiState.value.copy(age = age)
    }
    
    fun updateGender(gender: Gender) {
        _uiState.value = _uiState.value.copy(gender = gender)
    }
    
    fun updateHeight(height: Float) {
        _uiState.value = _uiState.value.copy(height = height)
    }
    
    fun updateWeight(weight: Float) {
        _uiState.value = _uiState.value.copy(weight = weight)
    }
    
    // Health Goals
    fun updateHealthGoal(healthGoal: HealthGoal) {
        _uiState.value = _uiState.value.copy(primaryGoal = healthGoal)
    }
    
    fun toggleSecondaryGoal(goal: HealthGoal) {
        val currentGoals = _uiState.value.secondaryGoals.toMutableList()
        if (currentGoals.contains(goal)) {
            currentGoals.remove(goal)
        } else {
            currentGoals.add(goal)
        }
        _uiState.value = _uiState.value.copy(secondaryGoals = currentGoals)
    }
    
    // Activity & Lifestyle
    fun updateActivityLevel(level: ActivityLevel) {
        _uiState.value = _uiState.value.copy(activityLevel = level)
    }
    
    fun toggleExercisePreference(preference: ExercisePreference) {
        val currentPreferences = _uiState.value.exercisePreferences.toMutableList()
        if (currentPreferences.contains(preference)) {
            currentPreferences.remove(preference)
        } else {
            currentPreferences.add(preference)
        }
        _uiState.value = _uiState.value.copy(exercisePreferences = currentPreferences)
    }
    
    fun updateDailyWellnessTime(time: DailyWellnessTime) {
        _uiState.value = _uiState.value.copy(dailyWellnessTime = time)
    }
    
    fun updateSleepHours(hours: Int) {
        _uiState.value = _uiState.value.copy(sleepHours = hours)
    }
    
    fun updateSleepPattern(pattern: SleepPattern) {
        _uiState.value = _uiState.value.copy(sleepPattern = pattern)
    }
    
    // Dietary Preferences
    fun updateDietaryPreference(preference: DietaryPreference) {
        _uiState.value = _uiState.value.copy(dietaryPreference = preference)
    }
    
    fun updateDietStyle(style: DietStyle) {
        _uiState.value = _uiState.value.copy(dietStyle = style)
    }
    
    // Mental Wellness
    fun updateStressLevel(level: StressLevel) {
        _uiState.value = _uiState.value.copy(stressLevel = level)
    }
    
    fun toggleMoodFocus(focus: MoodFocus) {
        val currentFocus = _uiState.value.moodFocusAreas.toMutableList()
        if (currentFocus.contains(focus)) {
            currentFocus.remove(focus)
        } else {
            currentFocus.add(focus)
        }
        _uiState.value = _uiState.value.copy(moodFocusAreas = currentFocus)
    }
    
    fun updateMindfulnessExperience(experience: MindfulnessExperience) {
        _uiState.value = _uiState.value.copy(mindfulnessExperience = experience)
    }
    
    // Environment & Habits
    fun updateWorkStyle(style: WorkStyle) {
        _uiState.value = _uiState.value.copy(workStyle = style)
    }
    
    fun updateScreenTime(time: ScreenTime) {
        _uiState.value = _uiState.value.copy(screenTime = time)
    }
    
    fun updateSmokingHabit(habit: SmokingHabit?) {
        _uiState.value = _uiState.value.copy(smokingHabit = habit)
    }
    
    fun updateAlcoholHabit(habit: AlcoholHabit?) {
        _uiState.value = _uiState.value.copy(alcoholHabit = habit)
    }
    
    // Personal Preferences
    fun toggleFavoriteActivity(activity: FavoriteActivity) {
        val currentActivities = _uiState.value.favoriteActivities.toMutableList()
        if (currentActivities.contains(activity)) {
            currentActivities.remove(activity)
        } else {
            currentActivities.add(activity)
        }
        _uiState.value = _uiState.value.copy(favoriteActivities = currentActivities)
    }
    
    fun updateMotivationStyle(style: MotivationStyle) {
        _uiState.value = _uiState.value.copy(motivationStyle = style)
    }
    
    fun updateExtraInformation(information: String) {
        _uiState.value = _uiState.value.copy(extraInformation = information)
    }
    
    fun saveProfile() {
        val currentState = _uiState.value
        if (isValidProfile(currentState)) {
            viewModelScope.launch {
                _uiState.value = currentState.copy(isLoading = true)
                
                try {
                    val userProfile = UserProfile(
                        // Basic Demographics
                        name = currentState.name,
                        age = currentState.age,
                        gender = currentState.gender,
                        height = currentState.height,
                        weight = currentState.weight,
                        bmi = currentState.bmi,
                        
                        // Health & Fitness Goals
                        primaryGoal = currentState.primaryGoal,
                        secondaryGoals = currentState.secondaryGoals,
                        
                        // Activity & Lifestyle
                        activityLevel = currentState.activityLevel,
                        exercisePreferences = currentState.exercisePreferences,
                        dailyWellnessTime = currentState.dailyWellnessTime,
                        sleepHours = currentState.sleepHours,
                        sleepPattern = currentState.sleepPattern,
                        
                        // Dietary Preferences
                        dietaryPreference = currentState.dietaryPreference,
                        foodAllergies = currentState.foodAllergies,
                        dietStyle = currentState.dietStyle,
                        
                        // Mental & Emotional Wellness
                        stressLevel = currentState.stressLevel,
                        moodFocusAreas = currentState.moodFocusAreas,
                        mindfulnessExperience = currentState.mindfulnessExperience,
                        
                        // Environment & Habits
                        workStyle = currentState.workStyle,
                        screenTime = currentState.screenTime,
                        smokingHabit = currentState.smokingHabit,
                        alcoholHabit = currentState.alcoholHabit,
                        
                        // Health Conditions
                        healthConditions = currentState.healthConditions,
                        physicalLimitations = currentState.physicalLimitations,
                        
                        // Personal Preferences
                        favoriteActivities = currentState.favoriteActivities,
                        motivationStyle = currentState.motivationStyle,
                        extraInformation = currentState.extraInformation
                    )
                    repository.saveUserProfile(userProfile)
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        isSaved = true
                    )
                } catch (e: Exception) {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        } else {
            _uiState.value = currentState.copy(
                error = "Please fill in all required fields (Name, Age, Primary Goal)"
            )
        }
    }
    
    fun loadExistingProfile() {
        viewModelScope.launch {
            repository.getUserProfile().collect { profile ->
                profile?.let {
                    _uiState.value = _uiState.value.copy(
                        // Basic Demographics
                        name = it.name,
                        age = it.age,
                        gender = it.gender,
                        height = it.height,
                        weight = it.weight,
                        bmi = it.bmi,
                        
                        // Health & Fitness Goals
                        primaryGoal = it.primaryGoal,
                        secondaryGoals = it.secondaryGoals,
                        
                        // Activity & Lifestyle
                        activityLevel = it.activityLevel,
                        exercisePreferences = it.exercisePreferences,
                        dailyWellnessTime = it.dailyWellnessTime,
                        sleepHours = it.sleepHours,
                        sleepPattern = it.sleepPattern,
                        
                        // Dietary Preferences
                        dietaryPreference = it.dietaryPreference,
                        foodAllergies = it.foodAllergies,
                        dietStyle = it.dietStyle,
                        
                        // Mental & Emotional Wellness
                        stressLevel = it.stressLevel,
                        moodFocusAreas = it.moodFocusAreas,
                        mindfulnessExperience = it.mindfulnessExperience,
                        
                        // Environment & Habits
                        workStyle = it.workStyle,
                        screenTime = it.screenTime,
                        smokingHabit = it.smokingHabit,
                        alcoholHabit = it.alcoholHabit,
                        
                        // Health Conditions
                        healthConditions = it.healthConditions,
                        physicalLimitations = it.physicalLimitations,
                        
                        // Personal Preferences
                        favoriteActivities = it.favoriteActivities,
                        motivationStyle = it.motivationStyle,
                        extraInformation = it.extraInformation,
                        
                        isSaved = true,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun isValidProfile(state: ProfileUiState): Boolean {
        return state.age > 0 && state.age <= 120 &&
                state.name.isNotBlank() &&
                state.primaryGoal != HealthGoal.GENERAL_WELLNESS // user must select a specific goal
    }


    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class ProfileUiState(
    // Basic Demographics
    val name: String = "",
    val age: Int = 0,
    val gender: Gender = Gender.OTHER,
    val height: Float? = null,
    val weight: Float? = null,
    val bmi: Float? = null,
    
    // Health & Fitness Goals
    val primaryGoal: HealthGoal = HealthGoal.GENERAL_WELLNESS,
    val secondaryGoals: List<HealthGoal> = emptyList(),
    
    // Activity & Lifestyle
    val activityLevel: ActivityLevel = ActivityLevel.SEDENTARY,
    val exercisePreferences: List<ExercisePreference> = emptyList(),
    val dailyWellnessTime: DailyWellnessTime = DailyWellnessTime.FIFTEEN_TO_THIRTY_MIN,
    val sleepHours: Int? = null,
    val sleepPattern: SleepPattern? = null,
    
    // Dietary Preferences
    val dietaryPreference: DietaryPreference = DietaryPreference.NON_VEGETARIAN,
    val foodAllergies: List<String> = emptyList(),
    val dietStyle: DietStyle? = null,
    
    // Mental & Emotional Wellness
    val stressLevel: StressLevel = StressLevel.MODERATE,
    val moodFocusAreas: List<MoodFocus> = emptyList(),
    val mindfulnessExperience: MindfulnessExperience = MindfulnessExperience.BEGINNER,
    
    // Environment & Habits
    val workStyle: WorkStyle = WorkStyle.DESK_JOB,
    val screenTime: ScreenTime = ScreenTime.MEDIUM,
    val smokingHabit: SmokingHabit? = null,
    val alcoholHabit: AlcoholHabit? = null,
    
    // Health Conditions
    val healthConditions: List<String> = emptyList(),
    val physicalLimitations: List<String> = emptyList(),
    
    // Personal Preferences
    val favoriteActivities: List<FavoriteActivity> = emptyList(),
    val motivationStyle: MotivationStyle = MotivationStyle.SHORT_ACTIONABLE,
    val extraInformation: String = "",
    
    // UI State
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

