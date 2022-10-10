package com.example.penca.network

import com.example.penca.BuildConfig
import com.example.penca.network.entities.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

object RetrofitFactory {

    fun getBuilder(on401Response: () -> Unit, getToken: () -> String?): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                // Request
                val token = getToken.invoke()
                val requestBuilder = chain.request().newBuilder()
                if (token != null) {
                    requestBuilder.addHeader("AUTHORIZATION", "Bearer $token")
                }
                val response = chain.proceed(requestBuilder.build())
                // Response
                if (response.code == 401) {
                    on401Response.invoke()
                }
                response
            })

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            client.addInterceptor(logging)
        }

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("https://api.penca.inhouse.decemberlabs.com")
            .client(client.build())
            .build()
    }
}
