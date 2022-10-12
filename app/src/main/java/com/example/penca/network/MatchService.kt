package com.example.penca.network

import com.example.penca.network.entities.*
import retrofit2.http.*

interface MatchService {

    @GET("/api/v1/files/")
    suspend fun getBannersUrl(): BannersResponse

    @GET("/api/v1/match")
    suspend fun getMatches(
        @Query("page") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("teamName") teamName: String? = null,
        @Query("status") status: String? = null,
        @Query("order") order: String = "DESC"
    ): MatchesContainer

    @GET("/api/v1/match/{matchId}")
    suspend fun getMatchDetails(
        @Path("matchId") matchId: Int,
    ): SeeDetailsBet


    @PATCH("/api/v1/match/{matchId}")
    suspend fun placeBet(
        @Path("matchId") matchId: Int,
        @Body betBody: BetBody,
    ): BetAnswer
}