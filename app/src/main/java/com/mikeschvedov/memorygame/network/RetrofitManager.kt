package com.mikeschvedov.memorygame.network

import com.mikeschvedov.memorygame.network.network_models.Letter
import com.mikeschvedov.memorygame.network.network_models.Number
import com.mikeschvedov.memorygame.utils.Logger
import retrofit2.HttpException
import java.io.IOException


const val TAG = "RetrofitManager"

class RetrofitManager(
    private val listener: ApiResultCallback,
) {
    suspend fun getLettersResponse() {
        val response = try {
            RetrofitInstance.api.getLetters()
        } catch (e: IOException) {
            Logger.e(TAG, "IOException, you might not have internet connection")
            return
        } catch (e: HttpException) {
            Logger.e(TAG, "HttpException, unexpected response")
            return
        }
        if (response.isSuccessful && response.body() != null) {

         //   Logger.e(TAG, response.body()!!.letters.toString())
            // getting the network response
            listener.onApiResult(null, response.body()!!.letters, true)
        } else {
            Logger.e(TAG, "Response not successful")
        }
    }

    suspend fun getNumbersResponse() {
        val response = try {
            RetrofitInstance.api.getNumbers()
        } catch (e: IOException) {
            Logger.e(TAG, "IOException, you might not have internet connection")
            return
        } catch (e: HttpException) {
            Logger.e(TAG, "HttpException, unexpected response")
            return
        }
        if (response.isSuccessful && response.body() != null) {
            // getting the network response
            listener.onApiResult(response.body()!!.numbers, null, true)
        } else {
            Logger.e(TAG, "Response not successful")
        }

    }

    fun interface ApiResultCallback {
        fun onApiResult(numbers: List<Number>?, letters: List<Letter>?, isReady: Boolean)
    }
}