package com.example.todoappshmr.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer Amrod")
            .addHeader("X-Last-Known-Revision", "32")
            .build()
        return chain.proceed(request)
    }
}