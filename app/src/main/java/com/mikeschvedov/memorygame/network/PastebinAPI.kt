package com.mikeschvedov.memorygame.network

import com.mikeschvedov.memorygame.network.network_models.LettersResponse
import com.mikeschvedov.memorygame.network.network_models.NumbersResponse
import retrofit2.Response
import retrofit2.http.GET

interface PastebinAPI {

    @GET("/raw/cZd0y0DL")
    suspend fun getLetters(): Response<LettersResponse>

    @GET("/raw/w8LSbsZb")
    suspend fun getNumbers(): Response<NumbersResponse>
}