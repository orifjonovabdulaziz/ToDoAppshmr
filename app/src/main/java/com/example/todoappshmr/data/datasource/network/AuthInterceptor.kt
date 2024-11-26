package com.example.todoappshmr.data.datasource.network

import android.content.Context
import com.example.todoappshmr.data.datasource.sharedPreferences.PreferencesManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val revision = PreferencesManager.getRevision(context)
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer Amrod")
            .addHeader("X-Last-Known-Revision", revision.toString())
            .build()
        return chain.proceed(request)
    }
}