package com.example.penca.domain.entities

import com.example.penca.database.DBMatch


enum class BetResult {
    Right,
    Wrong
}

enum class BetStatus {
    Done,
    NotDone
}

data class Bet(
    val match: Match,
    val status: BetStatus = BetStatus.NotDone,
    var homeGoalsBet: Int? = null,
    var awayGoalsBet: Int? = null
) {
    val result = if ((match.goalsLocal == homeGoalsBet) && (match.goalsAway == awayGoalsBet)) {
        BetResult.Right
    } else {
        BetResult.Wrong
    }

    fun asDBMatch() = DBMatch(
        this.match.id,
        this.match.homeTeam.id,
        this.match.homeTeam.name,
        this.match.homeTeam.image,
        this.match.awayTeam.id,
        this.match.awayTeam.name,
        this.match.awayTeam.image,
        this.match.date,
        this.match.goalsLocal,
        this.match.goalsAway,
        this.homeGoalsBet,
        this.awayGoalsBet
    )
}