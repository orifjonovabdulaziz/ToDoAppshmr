package com.example.todoappshmr.network

//import android.content.Context
import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

import javax.net.ssl.TrustManager
import java.security.SecureRandom


fun createSslContext(): Pair<SSLContext, X509TrustManager> {
    val trustManager = object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    }

    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, arrayOf(trustManager), SecureRandom())

    return Pair(sslContext, trustManager)
}

object RetrofitClient {
    fun create(context: Context): ApiService {
        val (sslContext, trustManager) = createSslContext()

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .addInterceptor(loggingInterceptor)
            .addInterceptor(ChuckerInterceptor(context))
//            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .hostnameVerifier { _, _ -> true }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://beta.mrdekk.ru/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}