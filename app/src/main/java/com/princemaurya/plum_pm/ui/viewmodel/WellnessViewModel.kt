package com.princemaurya.plum_pm.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.princemaurya.plum_pm.data.model.UserProfile
import com.princemaurya.plum_pm.data.model.WellnessTip
import com.princemaurya.plum_pm.data.repository.WellnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WellnessViewModel @Inject constructor(
    private val repository: WellnessRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    
    private val _uiState = MutableStateFlow(WellnessUiState())
    val uiState: StateFlow<WellnessUiState> = _uiState.asStateFlow()
    
    init {
        loadTips()
        checkForLanguageChange()
    }
    
    private fun checkForLanguageChange() {
        viewModelScope.launch {
            val languageChanged = prefs.getBoolean("language_changed", false)
            val forceRegenerate = prefs.getBoolean("force_regenerate_tips", false)
            
            if (languageChanged || forceRegenerate) {
                // Clear the flags
                prefs.edit().putBoolean("language_changed", false).apply()
                prefs.edit().putBoolean("force_regenerate_tips", false).apply()
                
                // Regenerate tips in new language if user profile exists
                val userProfile = _uiState.value.userProfile
                if (userProfile != null) {
                    Log.d("WellnessViewModel", "Language changed, regenerating tips for language sync")
                    generateNewTips()
                }
            }
        }
    }
    
    fun shouldRegenerateTips(): Boolean {
        return prefs.getBoolean("language_changed", false) || prefs.getBoolean("force_regenerate_tips", false)
    }
    
    fun forceRegenerateTips() {
        viewModelScope.launch {
            prefs.edit().putBoolean("force_regenerate_tips", true).apply()
            val userProfile = _uiState.value.userProfile
            if (userProfile != null) {
                Log.d("WellnessViewModel", "Force regenerating tips for language sync")
                generateNewTips()
            }
        }
    }
    
    fun generateNewTips() {
        val userProfile = _uiState.value.userProfile
        if (userProfile != null) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                repository.generateWellnessTips(userProfile)
                    .onSuccess { tips ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            tips = tips
                        )
                    }
                    .onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to generate tips"
                        )
                    }
            }
        }
    }
    
    fun expandTip(tip: WellnessTip) {
        val userProfile = _uiState.value.userProfile
        if (userProfile != null) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isExpandingTip = true)
                
                repository.expandTip(tip, userProfile)
                    .onSuccess { expandedTip ->
                        _uiState.value = _uiState.value.copy(
                            isExpandingTip = false,
                            expandedTip = expandedTip
                        )
                    }
                    .onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            isExpandingTip = false,
                            error = exception.message ?: "Failed to expand tip"
                        )
                    }
            }
        }
    }
    
    fun toggleFavorite(tip: WellnessTip) {
        viewModelScope.launch {
            repository.smartToggleFavorite(tip.id, !tip.isFavorite)
        }
    }
    
    fun loadFavoriteTips() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingFavorites = true)
            
            repository.getFavoriteTips().collect { favoriteTips ->
                Log.d("WellnessViewModel", "Loading ${favoriteTips.size} favorite tips")
                _uiState.value = _uiState.value.copy(
                    favoriteTips = favoriteTips,
                    isLoadingFavorites = false
                )
            }
        }
    }
    
    private fun loadTips() {
        viewModelScope.launch {
            repository.getAllTips().collect { tips ->
                _uiState.value = _uiState.value.copy(tips = tips)
            }
        }
        
        viewModelScope.launch {
            repository.getAllTipsIncludingFavorites().collect { allTips ->
                _uiState.value = _uiState.value.copy(allTips = allTips)
            }
        }
        
        viewModelScope.launch {
            repository.getUserProfile().collect { profile ->
                _uiState.value = _uiState.value.copy(userProfile = profile)
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearExpandedTip() {
        _uiState.value = _uiState.value.copy(expandedTip = null)
    }
    
    fun clearAllTips() {
        viewModelScope.launch {
            repository.clearAllTips()
        }
    }
}

data class WellnessUiState(
    val tips: List<WellnessTip> = emptyList(),
    val allTips: List<WellnessTip> = emptyList(),
    val favoriteTips: List<WellnessTip> = emptyList(),
    val expandedTip: WellnessTip? = null,
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val isExpandingTip: Boolean = false,
    val isLoadingFavorites: Boolean = false,
    val error: String? = null
)
