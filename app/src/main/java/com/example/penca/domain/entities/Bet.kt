package com.example.penca.domain.entities


enum class BetResult() {
    Right,
    Wrong
}

enum class BetStatus {
    Done,
    Pending
}

data class Bet(
    val match: Match,
    val status: BetStatus = BetStatus.Pending,
    val homeGoalsBet: Int? = null,
    val awayGoalsBet: Int? = null
) {
    val result = if ((match.goalsLocal == homeGoalsBet) && (match.goalsAway == awayGoalsBet)) {
        BetResult.Right
    } else {
        BetResult.Wrong
    }
}