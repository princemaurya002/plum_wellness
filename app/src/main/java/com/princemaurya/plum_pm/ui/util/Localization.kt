package com.princemaurya.plum_pm.ui.util

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
fun stringResourceLocalized(@StringRes id: Int, languageCode: String): String {
    val base = LocalContext.current
    val locale = when (languageCode.lowercase(Locale.getDefault())) {
        "hi" -> Locale("hi")
        "bn" -> Locale("bn")
        "ta" -> Locale("ta")
        "te" -> Locale("te")
        "mr" -> Locale("mr")
        else -> Locale("en")
    }
    val config = Configuration(base.resources.configuration)
    config.setLocale(locale)
    val localizedContext: Context = base.createConfigurationContext(config)
    return localizedContext.resources.getString(id)
}

// Optional helper: Avoid using this around subtrees that call hiltViewModel(),
// because overriding LocalContext breaks HiltViewModelFactory (needs Activity context).
@Composable
fun ProvideLocalizedResources(languageCode: String, content: @Composable () -> Unit) {
    val base = LocalContext.current
    val locale = when (languageCode.lowercase(Locale.getDefault())) {
        "hi" -> Locale("hi")
        "bn" -> Locale("bn")
        "ta" -> Locale("ta")
        "te" -> Locale("te")
        "mr" -> Locale("mr")
        else -> Locale("en")
    }
    val config = Configuration(base.resources.configuration)
    config.setLocale(locale)
    val localizedContext: Context = base.createConfigurationContext(config)
    CompositionLocalProvider(LocalContext provides localizedContext) {
        content()
    }
}
