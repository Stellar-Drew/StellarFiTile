package com.stellarfi.widget

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

object NetworkUtils {

    private val client = OkHttpClient()

    suspend fun simpleGetRequest(urlString: String): String? {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url(urlString)
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e("GET Request Failed", "Response code: ${response.code}")
                        return@withContext null
                    }

                    val responseBody = response.body?.string()
                    responseBody?.let {
                        Log.d("Token GET Success", it)
                        return@withContext it // Return the stored string
                    } ?: run {
                        Log.e("GET Request Failed", "Response body is null")
                        return@withContext null
                    }
                }
            } catch (e: IOException) {
                Log.e("GET Request Error", "Error during GET request: ${e.message}")
                null
            }
        }
    }
}