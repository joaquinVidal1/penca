package com.example.penca.mainscreen

import com.example.penca.domain.entities.BetResult
import com.example.penca.domain.entities.BetStatus
import com.example.penca.domain.entities.MatchStatus

enum class BetFilter {
    SeeAll,
    SeeAccerted,
    SeeMissed,
    SeePending,
    SeePlayedWithNoResult;

    companion object {
        fun getBetStatusResultAndMatchStatus(betFilter: BetFilter): Triple<BetStatus?, BetResult?, MatchStatus?> {
            return when (betFilter) {
                SeeAccerted -> Triple(BetStatus.Done, BetResult.Right, MatchStatus.Played)
                SeeMissed -> Triple(BetStatus.Done, BetResult.Wrong, MatchStatus.Played)
                SeePending -> Triple(null, null, MatchStatus.Pending)
                SeePlayedWithNoResult -> Triple(BetStatus.NotDone, null, MatchStatus.Played)
                SeeAll -> Triple(null, null, null)
            }
        }

        fun getStringForApi(betFilter: BetFilter): String? {
            return when (betFilter) {
                SeeAll -> null
                SeeAccerted -> "correct"
                SeeMissed -> "incorrect"
                SeePending -> "pending"
                SeePlayedWithNoResult -> "not_predicted"
            }
        }
    }
}