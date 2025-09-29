package com.princemaurya.plum_pm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.princemaurya.plum_pm.data.repository.WellnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartupViewModel @Inject constructor(
    private val repository: WellnessRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(StartupUiState())
    val uiState: StateFlow<StartupUiState> = _uiState.asStateFlow()
    
    init {
        checkProfileExists()
    }
    
    private fun checkProfileExists() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val profile = repository.getUserProfile().first()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    hasProfile = profile != null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    hasProfile = false
                )
            }
        }
    }
}

data class StartupUiState(
    val isLoading: Boolean = true,
    val hasProfile: Boolean = false
)
