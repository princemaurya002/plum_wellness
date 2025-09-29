package com.princemaurya.plum_pm.data.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.princemaurya.plum_pm.data.api.GeminiApiService
import com.princemaurya.plum_pm.data.api.TranslationApiService
import com.princemaurya.plum_pm.data.api.TranslateRequest
import com.princemaurya.plum_pm.config.ApiConfig
import androidx.core.text.HtmlCompat
import com.princemaurya.plum_pm.data.database.UserProfileDao
import com.princemaurya.plum_pm.data.database.WellnessTipDao
import com.princemaurya.plum_pm.data.model.UserProfile
import com.princemaurya.plum_pm.data.model.WellnessTip
import com.princemaurya.plum_pm.data.model.GeminiTipsResponse
import com.princemaurya.plum_pm.data.model.GeminiExpansionResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WellnessRepository @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val wellnessTipDao: WellnessTipDao,
    private val geminiApiService: GeminiApiService,
    private val translationApiService: TranslationApiService
) {
    private val gson = Gson()
    
    // User Profile operations
    fun getUserProfile(): Flow<UserProfile?> = userProfileDao.getUserProfile()
    
    suspend fun saveUserProfile(userProfile: UserProfile) {
        userProfileDao.insertUserProfile(userProfile)
    }
    
    // Wellness Tips operations
    fun getAllTips(): Flow<List<WellnessTip>> = wellnessTipDao.getAllTips()
    
    fun getAllTipsIncludingFavorites(): Flow<List<WellnessTip>> = wellnessTipDao.getAllTipsIncludingFavorites()
    
    fun getFavoriteTips(): Flow<List<WellnessTip>> = wellnessTipDao.getFavoriteTips()
    
    suspend fun generateWellnessTips(userProfile: UserProfile): Result<List<WellnessTip>> {
        // Get existing favorites before marking as old generation
        val existingFavorites = getFavoriteTipsSync()
        Log.d("WellnessRepository", "Found ${existingFavorites.size} existing favorites to preserve")
        
        return try {
            Log.d("WellnessRepository", "Generating tips for user: ${userProfile.name}")
            Log.d("WellnessRepository", "Using API key: ${ApiConfig.GEMINI_API_KEY.take(10)}...")
            
            // Mark all current tips as old generation (preserve favorites)
            Log.d("WellnessRepository", "Marking current tips as old generation")
            wellnessTipDao.markAllAsOldGeneration()
            
            val prompt = createTipsPrompt(userProfile)
            val request = createGeminiRequest(prompt)
            
            Log.d("WellnessRepository", "Making API call to Gemini")
            val response = geminiApiService.generateContent(
                apiKey = ApiConfig.GEMINI_API_KEY,
                request = request
            )
            
            Log.d("WellnessRepository", "API response code: ${response.code()}")
            
            if (response.isSuccessful) {
                val content = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                Log.d("WellnessRepository", "Received content length: ${content.length}")
                val tips = parseTipsFromResponse(content)
                Log.d("WellnessRepository", "Parsed ${tips.size} tips")
                // Save new tips to database (they will be marked as current generation)
                wellnessTipDao.insertTips(tips)
                
                // Verify favorites are still preserved after regeneration
                val remainingFavorites = getFavoriteTipsSync()
                Log.d("WellnessRepository", "After regeneration: ${remainingFavorites.size} favorites remain (was ${existingFavorites.size})")
                
                Result.success(tips)
            } else {
                val errorMessage = "Failed to generate tips: ${response.code()} - ${response.message()}"
                Log.e("WellnessRepository", errorMessage)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("WellnessRepository", "Exception in generateWellnessTips", e)
            // Fallback to mock tips if API fails
            Log.d("WellnessRepository", "Falling back to mock tips due to API error")
            val mockTips = generateMockTips()
            wellnessTipDao.insertTips(mockTips)
            
            // Verify favorites are still preserved after fallback
            val remainingFavorites = getFavoriteTipsSync()
            Log.d("WellnessRepository", "After fallback: ${remainingFavorites.size} favorites remain (was ${existingFavorites.size})")
            
            Result.success(mockTips)
        }
    }
    
    suspend fun expandTip(tip: WellnessTip, userProfile: UserProfile): Result<WellnessTip> {
        return try {
            val prompt = createExpansionPrompt(tip, userProfile)
            val request = createGeminiRequest(prompt)
            
            val response = geminiApiService.generateContent(
                apiKey = ApiConfig.GEMINI_API_KEY,
                request = request
            )
            
            if (response.isSuccessful) {
                val expandedContent = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                val expandedTip = parseExpandedTip(tip, expandedContent)
                // Update tip in database
                wellnessTipDao.updateTip(expandedTip)
                Result.success(expandedTip)
            } else {
                Result.failure(Exception("Failed to expand tip: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun toggleFavorite(tipId: String, isFavorite: Boolean) {
        wellnessTipDao.updateFavoriteStatus(tipId, isFavorite)
    }
    
    suspend fun smartToggleFavorite(tipId: String, isFavorite: Boolean) {
        if (isFavorite) {
            // Adding to favorites - just update the status
            Log.d("WellnessRepository", "Adding tip $tipId to favorites")
            wellnessTipDao.updateFavoriteStatus(tipId, true)
        } else {
            // Removing from favorites - check if tip is current generation
            val tip = wellnessTipDao.getTipById(tipId)
            if (tip != null) {
                Log.d("WellnessRepository", "Removing tip $tipId from favorites. Is current generation: ${tip.isCurrentGeneration}")

                if (tip.isCurrentGeneration) {
                    // Tip is current generation - just remove from favorites (stays on main screen)
                    Log.d("WellnessRepository", "Tip $tipId is current generation - removing from favorites only")
                    wellnessTipDao.updateFavoriteStatus(tipId, false)
                } else {
                    // Tip is old generation - delete it entirely
                    Log.d("WellnessRepository", "Tip $tipId is old generation - deleting entirely")
                    wellnessTipDao.deleteTip(tip)
                }
            } else {
                Log.w("WellnessRepository", "Tip $tipId not found when trying to toggle favorite")
            }
        }
    }
    
    private suspend fun getAllTipsSync(): List<WellnessTip> {
        return try {
            getAllTips().first()
        } catch (e: Exception) {
            Log.e("WellnessRepository", "Error getting current tips", e)
            emptyList()
        }
    }
    
    private suspend fun getFavoriteTipsSync(): List<WellnessTip> {
        return try {
            getFavoriteTips().first()
        } catch (e: Exception) {
            Log.e("WellnessRepository", "Error getting favorite tips", e)
            emptyList()
        }
    }
    
    
    suspend fun clearAllTips() {
        Log.d("WellnessRepository", "Clearing all tips")
        wellnessTipDao.deleteAllTips()
    }
    
    private fun getLanguageDisplayName(locale: Locale): String {
        return when (locale.language) {
            "hi" -> "Hindi"
            "bn" -> "Bengali"
            "ta" -> "Tamil"
            "te" -> "Telugu"
            "mr" -> "Marathi"
            else -> "English"
        }
    }
    
    
    private fun createTipsPrompt(userProfile: UserProfile): String {
        val locale: Locale = Locale.getDefault()
        val displayLanguage: String = getLanguageDisplayName(locale)
        Log.d("WellnessRepository", "Generating tips in language: $displayLanguage (locale: ${locale.language})")
        val bmiInfo = if (userProfile.bmi != null) " (BMI: ${String.format("%.1f", userProfile.bmi)})" else ""
        val heightWeightInfo = if (userProfile.height != null && userProfile.weight != null) {
            " (${userProfile.height}cm, ${userProfile.weight}kg)"
        } else ""
        
        return """
        Generate 5 highly personalized wellness tips for a ${userProfile.age}-year-old ${userProfile.gender.name.lowercase()}$heightWeightInfo$bmiInfo 
        whose primary health goal is ${userProfile.primaryGoal.displayName}.
        
        IMPORTANT: Write all textual content in $displayLanguage. Do not include any other language. Keep the JSON keys in English, but localize all values.
        
        User Profile Details:
        - Primary Goal: ${userProfile.primaryGoal.displayName}
        ${if (userProfile.secondaryGoals.isNotEmpty()) "- Secondary Goals: ${userProfile.secondaryGoals.joinToString(", ") { it.displayName }}" else ""}
        - Activity Level: ${userProfile.activityLevel.displayName}
        - Exercise Preferences: ${if (userProfile.exercisePreferences.isNotEmpty()) userProfile.exercisePreferences.joinToString(", ") { it.displayName } else "None specified"}
        - Daily Wellness Time: ${userProfile.dailyWellnessTime.displayName}
        - Dietary Preference: ${userProfile.dietaryPreference.displayName}
        ${if (userProfile.dietStyle != null) "- Diet Style: ${userProfile.dietStyle.displayName}" else ""}
        - Stress Level: ${userProfile.stressLevel.displayName}
        - Mindfulness Experience: ${userProfile.mindfulnessExperience.displayName}
        - Work Style: ${userProfile.workStyle.displayName}
        - Screen Time: ${userProfile.screenTime.displayName}
        - Motivation Style: ${userProfile.motivationStyle.displayName}
        ${if (userProfile.favoriteActivities.isNotEmpty()) "- Favorite Activities: ${userProfile.favoriteActivities.joinToString(", ") { it.displayName }}" else ""}
        ${if (userProfile.extraInformation.isNotBlank()) "- Additional Info: ${userProfile.extraInformation}" else ""}
        
        Please return the response in the following JSON format:
        {
          "tips": [
            {
              "id": "unique_id_1",
              "title": "Tip Title",
              "summary": "One line summary",
              "detailedExplanation": "Brief explanation",
              "stepByStepGuide": ["Step 1", "Step 2", "Step 3"],
              "category": "Category name",
              "icon": "emoji_icon"
            }
          ]
        }
        
        Make the tips:
        - Highly personalized based on their specific profile
        - Practical and actionable for their lifestyle and time constraints
        - Tailored to their activity level, dietary preferences, and work style
        - Appropriate for their stress level and mindfulness experience
        - Aligned with their motivation style (${userProfile.motivationStyle.displayName})
        - Evidence-based wellness practices that are safe and effective
        - Consider their exercise preferences and favorite activities
        - Address their specific health goals and any secondary goals
        """.trimIndent()
    }
    
    private fun createExpansionPrompt(tip: WellnessTip, userProfile: UserProfile): String {
        val locale: Locale = Locale.getDefault()
        val displayLanguage: String = getLanguageDisplayName(locale)
        Log.d("WellnessRepository", "Expanding tip in language: $displayLanguage (locale: ${locale.language})")
        return """
        Expand this wellness tip with detailed information:
        
        Original tip: ${tip.title}
        Summary: ${tip.summary}
        
        User profile: ${userProfile.age}-year-old ${userProfile.gender.name.lowercase()}
        - Primary Goal: ${userProfile.primaryGoal.displayName}
        - Activity Level: ${userProfile.activityLevel.displayName}
        - Exercise Preferences: ${if (userProfile.exercisePreferences.isNotEmpty()) userProfile.exercisePreferences.joinToString(", ") { it.displayName } else "None specified"}
        - Daily Wellness Time: ${userProfile.dailyWellnessTime.displayName}
        - Dietary Preference: ${userProfile.dietaryPreference.displayName}
        - Stress Level: ${userProfile.stressLevel.displayName}
        - Work Style: ${userProfile.workStyle.displayName}
        - Motivation Style: ${userProfile.motivationStyle.displayName}
        ${if (userProfile.extraInformation.isNotBlank()) "- Additional Info: ${userProfile.extraInformation}" else ""}
        
        IMPORTANT: Write all textual content in $displayLanguage. Do not include any other language. Keep the JSON keys in English, but localize all values.
        
        Please provide a detailed explanation and step-by-step guide. Return in JSON format:
        {
          "detailedExplanation": "Comprehensive explanation of the tip with scientific backing, tailored to their specific profile",
          "stepByStepGuide": ["Detailed step 1", "Detailed step 2", "Detailed step 3"]
        }
        
        Make the explanation:
        - Comprehensive and evidence-based
        - Highly personalized for their specific profile
        - Actionable within their time constraints (${userProfile.dailyWellnessTime.displayName})
        - Appropriate for their activity level and exercise preferences
        - Aligned with their motivation style and work environment
        - Consider their dietary preferences and stress level
        """.trimIndent()
    }
    
    private fun createGeminiRequest(prompt: String): com.princemaurya.plum_pm.data.api.GeminiRequest {
        return com.princemaurya.plum_pm.data.api.GeminiRequest(
            contents = listOf(
                com.princemaurya.plum_pm.data.api.Content(
                    parts = listOf(
                        com.princemaurya.plum_pm.data.api.Part(text = prompt)
                    )
                )
            ),
            generationConfig = com.princemaurya.plum_pm.data.api.GenerationConfig(
                temperature = ApiConfig.TEMPERATURE,
                maxOutputTokens = ApiConfig.MAX_OUTPUT_TOKENS,
                topK = ApiConfig.TOP_K,
                topP = ApiConfig.TOP_P
            )
        )
    }
    
    private fun parseTipsFromResponse(response: String): List<WellnessTip> {
        return try {
            // Try to parse JSON response from Gemini
            if (response.contains("{") && response.contains("tips")) {
                // Extract JSON from response
                val jsonStart = response.indexOf("{")
                val jsonEnd = response.lastIndexOf("}") + 1
                val jsonString = response.substring(jsonStart, jsonEnd)
                
                val geminiResponse = gson.fromJson(jsonString, GeminiTipsResponse::class.java)
                geminiResponse.tips.map { geminiTip ->
                    WellnessTip(
                        id = geminiTip.id,
                        title = geminiTip.title,
                        summary = geminiTip.summary,
                        detailedExplanation = geminiTip.detailedExplanation,
                        stepByStepGuide = geminiTip.stepByStepGuide,
                        category = geminiTip.category,
                        icon = geminiTip.icon
                    )
                }
            } else {
                // If response doesn't contain JSON, generate tips based on response content
                generateMockTips()
            }
        } catch (e: JsonSyntaxException) {
            // If JSON parsing fails, fallback to mock tips
            generateMockTips()
        } catch (e: Exception) {
            // Fallback to mock tips
            generateMockTips()
        }
    }
    
    private fun generateMockTips(): List<WellnessTip> {
        return listOf(
            WellnessTip(
                id = "tip_${System.currentTimeMillis()}_1",
                title = "Morning Hydration Boost",
                summary = "Start your day with a glass of water to kickstart your metabolism",
                detailedExplanation = "Drinking water first thing in the morning helps rehydrate your body after a night's sleep and can boost your metabolism by up to 30% for about an hour. This simple habit also helps flush out toxins and prepares your digestive system for the day ahead.",
                stepByStepGuide = listOf(
                    "Keep a glass of water by your bedside before sleeping",
                    "Drink the entire glass within 10 minutes of waking up",
                    "Wait 30 minutes before having your first meal or coffee"
                ),
                category = "Hydration",
                icon = "💧"
            ),
            WellnessTip(
                id = "tip_${System.currentTimeMillis()}_2",
                title = "10-Minute Morning Stretch",
                summary = "Gentle stretching routine to improve flexibility and energy",
                detailedExplanation = "A brief morning stretching routine can improve blood circulation, reduce muscle stiffness, and increase your energy levels throughout the day. It also helps prepare your body for daily activities and reduces the risk of injury.",
                stepByStepGuide = listOf(
                    "Start with neck rolls and shoulder shrugs",
                    "Do gentle spinal twists while seated or standing",
                    "Finish with deep breathing exercises for 2-3 minutes"
                ),
                category = "Fitness",
                icon = "🧘"
            ),
            WellnessTip(
                id = "tip_${System.currentTimeMillis()}_3",
                title = "Mindful Eating Practice",
                summary = "Eat slowly and mindfully to improve digestion and satisfaction",
                detailedExplanation = "Mindful eating involves paying full attention to the experience of eating and drinking. This practice can help you eat less, enjoy food more, and develop a healthier relationship with food. It also improves digestion and nutrient absorption.",
                stepByStepGuide = listOf(
                    "Remove distractions like TV or phone while eating",
                    "Take small bites and chew each mouthful 20-30 times",
                    "Pause between bites and check in with your hunger levels"
                ),
                category = "Nutrition",
                icon = "🍽️"
            ),
            WellnessTip(
                id = "tip_${System.currentTimeMillis()}_4",
                title = "Evening Wind-Down Routine",
                summary = "Create a relaxing bedtime routine for better sleep quality",
                detailedExplanation = "A consistent wind-down routine signals to your body that it's time to sleep, helping you fall asleep faster and enjoy deeper, more restorative sleep. This is especially important for weight management as poor sleep can disrupt hunger hormones.",
                stepByStepGuide = listOf(
                    "Stop using electronic devices 1 hour before bed",
                    "Do a calming activity like reading or gentle stretching",
                    "Keep your bedroom cool, dark, and quiet"
                ),
                category = "Sleep",
                icon = "🌙"
            ),
            WellnessTip(
                id = "tip_${System.currentTimeMillis()}_5",
                title = "Daily Gratitude Practice",
                summary = "Write down three things you're grateful for each day",
                detailedExplanation = "Practicing gratitude has been shown to improve mental health, reduce stress, and even boost physical health. It helps shift your focus from what's lacking to what's abundant in your life, creating a positive mindset that supports your wellness goals.",
                stepByStepGuide = listOf(
                    "Set aside 5 minutes each morning or evening",
                    "Write down three specific things you're grateful for",
                    "Reflect on why each item brings you joy or appreciation"
                ),
                category = "Mental Health",
                icon = "🙏"
            )
        )
    }
    
    private fun parseExpandedTip(originalTip: WellnessTip, expandedContent: String): WellnessTip {
        return try {
            // Try to parse JSON response from Gemini
            if (expandedContent.contains("{") && expandedContent.contains("detailedExplanation")) {
                // Extract JSON from response
                val jsonStart = expandedContent.indexOf("{")
                val jsonEnd = expandedContent.lastIndexOf("}") + 1
                val jsonString = expandedContent.substring(jsonStart, jsonEnd)
                
                val expansionResponse = gson.fromJson(jsonString, GeminiExpansionResponse::class.java)
                originalTip.copy(
                    detailedExplanation = expansionResponse.detailedExplanation,
                    stepByStepGuide = expansionResponse.stepByStepGuide
                )
            } else {
                // Fallback to mock expansion
                originalTip.copy(
                    detailedExplanation = "This is a detailed explanation of ${originalTip.title}. " +
                            "It provides comprehensive guidance on how to implement this wellness practice in your daily routine.",
                    stepByStepGuide = listOf(
                        "Step 1: Prepare yourself mentally and physically",
                        "Step 2: Follow the specific techniques outlined",
                        "Step 3: Monitor your progress and adjust as needed"
                    )
                )
            }
        } catch (e: JsonSyntaxException) {
            // If JSON parsing fails, fallback to mock expansion
            originalTip.copy(
                detailedExplanation = "This is a detailed explanation of ${originalTip.title}. " +
                        "It provides comprehensive guidance on how to implement this wellness practice in your daily routine.",
                stepByStepGuide = listOf(
                    "Step 1: Prepare yourself mentally and physically",
                    "Step 2: Follow the specific techniques outlined",
                    "Step 3: Monitor your progress and adjust as needed"
                )
            )
        } catch (e: Exception) {
            // Fallback to mock expansion
            originalTip.copy(
                detailedExplanation = "This is a detailed explanation of ${originalTip.title}. " +
                        "It provides comprehensive guidance on how to implement this wellness practice in your daily routine.",
                stepByStepGuide = listOf(
                    "Step 1: Prepare yourself mentally and physically",
                    "Step 2: Follow the specific techniques outlined",
                    "Step 3: Monitor your progress and adjust as needed"
                )
            )
        }
    }

    /**
     * Translates all current tips' textual fields to the target language using Google Cloud Translation API
     * and persists the translated versions, replacing existing text.
     */
    suspend fun translateExistingTips(targetLanguageCode: String): Result<Unit> {
        return try {
            val tips = getAllTipsSync()
            if (tips.isEmpty()) return Result.success(Unit)

            for (tip in tips) {
                val sourceTexts = buildList {
                    add(tip.title)
                    add(tip.summary)
                    add(tip.detailedExplanation)
                    addAll(tip.stepByStepGuide)
                    add(tip.category)
                }

                // Skip empty to avoid API errors
                val nonEmpty = sourceTexts.map { it.ifBlank { " " } }

                val response = translationApiService.translate(
                    apiKey = ApiConfig.TRANSLATION_API_KEY,
                    request = TranslateRequest(
                        q = nonEmpty,
                        target = mapToGoogleTranslateCode(targetLanguageCode),
                        format = "text"
                    )
                )

                if (response.isSuccessful) {
                    val translations = response.body()?.data?.translations?.map { 
                        // Cloud Translation may return HTML-escaped text; decode to plain
                        HtmlCompat.fromHtml(it.translatedText, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                    } ?: emptyList()
                    if (translations.size >= sourceTexts.size) {
                        var idx = 0
                        val newTitle = translations[idx++]
                        val newSummary = translations[idx++]
                        val newDetailed = translations[idx++]
                        val newSteps = mutableListOf<String>()
                        repeat(tip.stepByStepGuide.size) { newSteps.add(translations[idx++]) }
                        val newCategory = translations[idx]

                        val translatedTip = tip.copy(
                            title = newTitle,
                            summary = newSummary,
                            detailedExplanation = newDetailed,
                            stepByStepGuide = newSteps,
                            category = newCategory
                        )
                        wellnessTipDao.updateTip(translatedTip)
                    } else {
                        Log.w("WellnessRepository", "Insufficient translations for tip ${'$'}{tip.id}")
                    }
                } else {
                    Log.e(
                        "WellnessRepository",
                        "Translation failed: ${'$'}{response.code()} - ${'$'}{response.message()}"
                    )
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("WellnessRepository", "Exception in translateExistingTips", e)
            Result.failure(e)
        }
    }

    private fun mapToGoogleTranslateCode(appLang: String): String {
        return when (appLang.lowercase(Locale.getDefault())) {
            "en" -> "en"
            "hi" -> "hi"
            "bn" -> "bn"
            "ta" -> "ta"
            "te" -> "te"
            "mr" -> "mr"
            else -> "en"
        }
    }
}
