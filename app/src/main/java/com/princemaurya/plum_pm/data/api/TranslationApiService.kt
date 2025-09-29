package com.princemaurya.plum_pm.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface TranslationApiService {
    @POST("language/translate/v2")
    suspend fun translate(
        @Query("key") apiKey: String,
        @Body request: TranslateRequest
    ): Response<TranslateResponse>
}

data class TranslateRequest(
    val q: List<String>,
    val target: String,
    val format: String = "text"
)

data class TranslateResponse(
    val data: TranslationsData
)

data class TranslationsData(
    val translations: List<TranslationItem>
)

data class TranslationItem(
    val translatedText: String
)
