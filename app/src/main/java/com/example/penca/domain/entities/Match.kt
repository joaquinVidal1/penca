package com.example.penca.domain.entities

import java.time.LocalDate

enum class MatchStatus {
    Played,
    Pending,
}

data class Match(
    val id: Int,
    val homeTeamId: Int,
    val awayTeamId: Int,
    val Status: MatchStatus = MatchStatus.Pending,
    val date: LocalDate,
    val goalsLocal: Int? = null,
    val goalsAway: Int? = null,
    val events: List<MatchEvent>? = null
) {}