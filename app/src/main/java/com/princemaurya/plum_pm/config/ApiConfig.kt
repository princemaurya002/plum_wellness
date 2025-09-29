package com.princemaurya.plum_pm.config

object ApiConfig {
    // In production, this should be stored securely (e.g., in build config or environment variables)
    // For now, we'll use a placeholder that should be replaced with your actual Gemini API key
    const val GEMINI_API_KEY = "AIzaSyAAYJ9lvUh6MtMIvJiIJo552bJ3lB4bDAc"
    // Translation API key; if you use a different Google Cloud project for Translation API,
    // set that key here. Defaults to GEMINI_API_KEY for convenience.
    const val TRANSLATION_API_KEY = "AIzaSyDZYNZki1y8JQK1Mi4A7i6kA-MBrCtSCqs"
    
    // Gemini API configuration
    const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/"
    const val GEMINI_MODEL = "gemini-2.5-flash"
    const val MAX_OUTPUT_TOKENS = 5000
    const val TEMPERATURE = 0.7
    const val TOP_K = 40
    const val TOP_P = 0.95
}
