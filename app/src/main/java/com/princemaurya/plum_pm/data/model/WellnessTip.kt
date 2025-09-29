package com.princemaurya.plum_pm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wellness_tips")
data class WellnessTip(
    @PrimaryKey
    val id: String,
    val title: String,
    val summary: String,
    val detailedExplanation: String,
    val stepByStepGuide: List<String>,
    val category: String,
    val icon: String,
    val isFavorite: Boolean = false,
    val isCurrentGeneration: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

data class TipRequest(
    val userProfile: UserProfile,
    val tipCount: Int = 5
)

data class TipResponse(
    val tips: List<WellnessTip>
)

data class DetailedTipRequest(
    val tip: WellnessTip,
    val userProfile: UserProfile
)
