package com.example.penca.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

enum class MatchStatus {
    Played,
    Pending,
}

data class Match(
    val id: Int,
    val homeTeam: Team,
    val awayTeam: Team,
    val date: LocalDate,
    val goalsLocal: Int? = null,
    val goalsAway: Int? = null,
    val events: List<MatchEvent>? = null
) {
    val status
        get() = if (date < LocalDate.now()) {
            MatchStatus.Played
        } else {
            MatchStatus.Pending
        }
}