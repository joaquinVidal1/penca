package com.example.penca.network

import com.example.penca.network.entities.AuthenticationBody
import com.example.penca.network.entities.NetworkUser
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {

    @POST("/api/v1/user/login")
    suspend fun logIn(
        @Body authenticationBody: AuthenticationBody
    ): NetworkUser

    @POST("/api/v1/user/")
    suspend fun register(
        @Body authenticationBody: AuthenticationBody
    ): NetworkUser

}