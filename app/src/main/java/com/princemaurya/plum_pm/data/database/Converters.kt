package com.princemaurya.plum_pm.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.princemaurya.plum_pm.data.model.*

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromHealthGoalList(value: List<HealthGoal>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toHealthGoalList(value: String): List<HealthGoal> {
        val listType = object : TypeToken<List<HealthGoal>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromExercisePreferenceList(value: List<ExercisePreference>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toExercisePreferenceList(value: String): List<ExercisePreference> {
        val listType = object : TypeToken<List<ExercisePreference>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromMoodFocusList(value: List<MoodFocus>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMoodFocusList(value: String): List<MoodFocus> {
        val listType = object : TypeToken<List<MoodFocus>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromFavoriteActivityList(value: List<FavoriteActivity>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toFavoriteActivityList(value: String): List<FavoriteActivity> {
        val listType = object : TypeToken<List<FavoriteActivity>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromSleepPattern(value: SleepPattern?): String? {
        return value?.name
    }

    @TypeConverter
    fun toSleepPattern(value: String?): SleepPattern? {
        return value?.let { SleepPattern.valueOf(it) }
    }

    @TypeConverter
    fun fromDietStyle(value: DietStyle?): String? {
        return value?.name
    }

    @TypeConverter
    fun toDietStyle(value: String?): DietStyle? {
        return value?.let { DietStyle.valueOf(it) }
    }

    @TypeConverter
    fun fromSmokingHabit(value: SmokingHabit?): String? {
        return value?.name
    }

    @TypeConverter
    fun toSmokingHabit(value: String?): SmokingHabit? {
        return value?.let { SmokingHabit.valueOf(it) }
    }

    @TypeConverter
    fun fromAlcoholHabit(value: AlcoholHabit?): String? {
        return value?.name
    }

    @TypeConverter
    fun toAlcoholHabit(value: String?): AlcoholHabit? {
        return value?.let { AlcoholHabit.valueOf(it) }
    }
}