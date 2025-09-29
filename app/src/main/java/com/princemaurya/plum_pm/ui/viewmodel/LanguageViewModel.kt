package com.princemaurya.plum_pm.ui.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.princemaurya.plum_pm.data.repository.WellnessRepository
import com.princemaurya.plum_pm.ui.components.SUPPORTED_LANGUAGES
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val wellnessRepository: WellnessRepository
) : ViewModel() {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    
    private val _currentLanguage = MutableStateFlow(getCurrentLanguage())
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()
    private val _isTranslating = MutableStateFlow(false)
    val isTranslating: StateFlow<Boolean> = _isTranslating.asStateFlow()
    private val _translationError = MutableStateFlow<String?>(null)
    val translationError: StateFlow<String?> = _translationError.asStateFlow()
    
    private fun getCurrentLanguage(): String {
        return prefs.getString("selected_language", "en") ?: "en"
    }
    
    fun setLanguage(languageCode: String, activity: Activity? = null) {
        viewModelScope.launch {
            val currentLang = prefs.getString("selected_language", "en") ?: "en"
            
            // Save new language
            prefs.edit().putString("selected_language", languageCode).apply()
            _currentLanguage.value = languageCode
            
            // Do not set any regeneration flags; tips will not auto-regenerate on language change
            // Instead, translate current tips in-place to the selected language
            if (currentLang != languageCode) {
                _isTranslating.value = true
                try {
                    val result = wellnessRepository.translateExistingTips(languageCode)
                    result.onFailure { e ->
                        _translationError.value = e.message ?: "Translation failed"
                    }
                } catch (e: Exception) {
                    _translationError.value = e.message ?: "Translation error"
                } finally {
                    // Apply language to system resources without restarting the activity
                    applyLanguageToSystem(languageCode)
                    _isTranslating.value = false
                }
            }
        }
    }

    fun clearTranslationError() {
        _translationError.value = null
    }
    
    private fun applyLanguageToSystem(languageCode: String) {
        val locale = when (languageCode) {
            "hi" -> Locale("hi")
            "bn" -> Locale("bn")
            "ta" -> Locale("ta")
            "te" -> Locale("te")
            "mr" -> Locale("mr")
            else -> Locale("en")
        }
        
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }
    
    fun getCurrentLanguageDisplayName(): String {
        val languageCode = _currentLanguage.value
        return SUPPORTED_LANGUAGES.find { it.code == languageCode }?.nativeName ?: "English"
    }
}
