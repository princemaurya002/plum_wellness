package com.princemaurya.plum_pm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey
    val id: Int = 1, // Single profile per app instance
    // Basic Demographics
    val name: String = "",
    val age: Int,
    val gender: Gender,
    val height: Float? = null, // in cm
    val weight: Float? = null, // in kg
    val bmi: Float? = null,
    
    // Health & Fitness Goals
    val primaryGoal: HealthGoal,
    val secondaryGoals: List<HealthGoal> = emptyList(),
    
    // Activity & Lifestyle
    val activityLevel: ActivityLevel,
    val exercisePreferences: List<ExercisePreference> = emptyList(),
    val dailyWellnessTime: DailyWellnessTime,
    val sleepHours: Int? = null,
    val sleepPattern: SleepPattern? = null,
    
    // Dietary Preferences
    val dietaryPreference: DietaryPreference,
    val foodAllergies: List<String> = emptyList(),
    val dietStyle: DietStyle? = null,
    
    // Mental & Emotional Wellness
    val stressLevel: StressLevel,
    val moodFocusAreas: List<MoodFocus> = emptyList(),
    val mindfulnessExperience: MindfulnessExperience,
    
    // Environment & Habits
    val workStyle: WorkStyle,
    val screenTime: ScreenTime,
    val smokingHabit: SmokingHabit? = null,
    val alcoholHabit: AlcoholHabit? = null,
    
    // Health Conditions
    val healthConditions: List<String> = emptyList(),
    val physicalLimitations: List<String> = emptyList(),
    
    // Personal Preferences
    val favoriteActivities: List<FavoriteActivity> = emptyList(),
    val motivationStyle: MotivationStyle,
    val extraInformation: String = ""
)

enum class Gender {
    MALE, FEMALE, OTHER
}

enum class HealthGoal(val displayName: String) {
    WEIGHT_LOSS("Weight Loss"),
    MUSCLE_GAIN("Muscle Gain"),
    STRESS_RELIEF("Stress Relief"),
    BETTER_SLEEP("Better Sleep"),
    FITNESS("Fitness"),
    NUTRITION("Nutrition"),
    MENTAL_HEALTH("Mental Health"),
    GENERAL_WELLNESS("General Wellness")
}

enum class ActivityLevel(val displayName: String) {
    SEDENTARY("Sedentary"),
    LIGHTLY_ACTIVE("Lightly Active"),
    MODERATELY_ACTIVE("Moderately Active"),
    HIGHLY_ACTIVE("Highly Active")
}

enum class ExercisePreference(val displayName: String) {
    YOGA("Yoga"),
    CARDIO("Cardio"),
    STRENGTH_TRAINING("Strength Training"),
    WALKING("Walking"),
    MEDITATION("Meditation"),
    PILATES("Pilates"),
    SWIMMING("Swimming"),
    RUNNING("Running"),
    CYCLING("Cycling"),
    DANCING("Dancing")
}

enum class DailyWellnessTime(val displayName: String) {
    FIVE_TO_TEN_MIN("5-10 minutes"),
    FIFTEEN_TO_THIRTY_MIN("15-30 minutes"),
    THIRTY_PLUS_MIN("30+ minutes")
}

enum class SleepPattern(val displayName: String) {
    EARLY_BIRD("Early Bird (sleep early, wake early)"),
    NIGHT_OWL("Night Owl (sleep late, wake late)"),
    REGULAR("Regular Schedule"),
    IRREGULAR("Irregular Schedule")
}

enum class DietaryPreference(val displayName: String) {
    VEGETARIAN("Vegetarian"),
    VEGAN("Vegan"),
    PESCATARIAN("Pescatarian"),
    NON_VEGETARIAN("Non-Vegetarian")
}

enum class DietStyle(val displayName: String) {
    KETO("Keto"),
    LOW_CARB("Low-Carb"),
    MEDITERRANEAN("Mediterranean"),
    BALANCED("Balanced"),
    INTERMITTENT_FASTING("Intermittent Fasting"),
    PALEO("Paleo")
}

enum class StressLevel(val displayName: String) {
    LOW("Low"),
    MODERATE("Moderate"),
    HIGH("High")
}

enum class MoodFocus(val displayName: String) {
    ANXIETY("Anxiety"),
    DEPRESSION("Depression"),
    MINDFULNESS("Mindfulness"),
    PRODUCTIVITY("Productivity"),
    CONFIDENCE("Confidence"),
    RELATIONSHIPS("Relationships")
}

enum class MindfulnessExperience(val displayName: String) {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced")
}

enum class WorkStyle(val displayName: String) {
    DESK_JOB("Desk Job"),
    FIELD_JOB("Field Job"),
    REMOTE("Remote"),
    HYBRID("Hybrid")
}

enum class ScreenTime(val displayName: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High")
}

enum class SmokingHabit(val displayName: String) {
    NON_SMOKER("Non-Smoker"),
    OCCASIONAL("Occasional"),
    REGULAR("Regular"),
    QUITTING("Quitting")
}

enum class AlcoholHabit(val displayName: String) {
    NON_DRINKER("Non-Drinker"),
    OCCASIONAL("Occasional"),
    MODERATE("Moderate"),
    REGULAR("Regular")
}

enum class FavoriteActivity(val displayName: String) {
    WALKING("Walking"),
    HIKING("Hiking"),
    GYM("Gym"),
    YOGA("Yoga"),
    MEDITATION("Meditation"),
    JOURNALING("Journaling"),
    READING("Reading"),
    COOKING("Cooking"),
    GARDENING("Gardening"),
    SPORTS("Sports")
}

enum class MotivationStyle(val displayName: String) {
    SHORT_ACTIONABLE("Short Actionable Tips"),
    LONG_EXPLANATIONS("Long Explanations"),
    STEP_BY_STEP("Step-by-Step Guides"),
    VISUAL("Visual Content"),
    AUDIO("Audio Content")
}
