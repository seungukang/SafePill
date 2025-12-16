package com.example.safepill

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AnalyzeApi {
    @Multipart
    @POST("/analyze-pill")
    suspend fun analyzePill(
        @Part image: MultipartBody.Part
    ): Response<AnalyzeResponse>
}

data class AnalyzeResponse(
    val name: String,
    val index: Int
)
