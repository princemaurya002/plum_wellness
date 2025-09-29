package com.princemaurya.plum_pm.util

object Constants {
    // API Configuration
    const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/"
    const val GEMINI_MODEL = "gemini-2.5-flash"
    const val MAX_TOKENS = 1000
    const val TEMPERATURE = 0.7
    
    // Database
    const val DATABASE_NAME = "wellness_database"
    const val DATABASE_VERSION = 1
    
    // UI Configuration
    const val ANIMATION_DURATION = 300
    const val CARD_ELEVATION = 4
    const val CARD_ROUNDED_CORNER = 16
    
    // Default Values
    const val DEFAULT_TIP_COUNT = 5
    const val DEFAULT_USER_ID = 1
    
    // Validation
    const val MIN_AGE = 1
    const val MAX_AGE = 120
    const val MIN_NAME_LENGTH = 2
    const val MAX_NAME_LENGTH = 50
}
