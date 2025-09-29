package com.princemaurya.plum_pm.data.model

// Data classes for JSON parsing from Gemini API
data class GeminiTipsResponse(
    val tips: List<GeminiTip>
)

data class GeminiTip(
    val id: String,
    val title: String,
    val summary: String,
    val detailedExplanation: String,
    val stepByStepGuide: List<String>,
    val category: String,
    val icon: String
)

data class GeminiExpansionResponse(
    val detailedExplanation: String,
    val stepByStepGuide: List<String>
)
