package com.example.penca.network

import com.example.penca.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import kotlin.reflect.jvm.internal.impl.types.checker.NewCapturedType

interface MatchService {

}

interface UserService {

    @FormUrlEncoded
    @POST("/api/v1/user/login")
    suspend fun logIn(
        @Field("email") email: String, @Field("password") password: String
    ): NetworkUser

    @FormUrlEncoded
    @POST("/api/v1/user/")
    suspend fun register(
        @Field("email") email: String, @Field("password") password: String
    ): NetworkUser

}

object UserNetwork {

    val user: UserService = getBuilder().create(UserService::class.java)

    private fun getBuilder(): Retrofit {
        val client = OkHttpClient.Builder()
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
