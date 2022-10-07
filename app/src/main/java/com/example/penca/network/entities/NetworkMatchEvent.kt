package com.example.penca.network.entities

import com.example.penca.domain.entities.MatchEvent
import com.example.penca.domain.entities.MatchEventKind
import com.example.penca.domain.entities.MatchTeams
import com.squareup.moshi.Json

data class NetworkMatchEvent(

    @Json(name = "minute")
    val minute: Int,

    @Json(name = "side")
    val side: String,

    @Json(name = "event")
    val event: String,

    @Json(name = "playerName")
    val playerName: String
) {
    private fun getMatchTeamFromString(side: String): MatchTeams {
        return if (side == "home") {
            MatchTeams.Home
        } else {
            MatchTeams.Away
        }

    }

    private fun getMatchEventKindFromString(event: String): MatchEventKind {
        return when (event) {
            "goal" -> MatchEventKind.Goal
            "red card" -> MatchEventKind.RedCard
            "yellow card" -> MatchEventKind.YellowCard
            else -> MatchEventKind.Goal
        }
    }

    fun getMatchEventFromNetworkEvent() =
        MatchEvent(
            this.minute,
            this.playerName,
            getMatchEventKindFromString(this.event),
            getMatchTeamFromString(this.side)
        )
}