package com.mikeschvedov.memorygame.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val api: PastebinAPI by lazy {
        Retrofit.Builder()
            .baseUrl("https://pastebin.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PastebinAPI::class.java)
    }


}