package com.example.penca.repository

import com.example.penca.R
import com.example.penca.domain.entities.*
import java.time.LocalDate

class MatchRepository() {
    val teamsList = listOf<Team>(
        Team(0, "Peñarol", R.drawable.escudo_penarol),
        Team(1, "Nacional", R.drawable.escudo_nacional),
        Team(2, "Maldonado", R.drawable.escudo_maldonado),
        Team(3, "Fénix", R.drawable.escudo_fenix),
        Team(4, "Boston River", R.drawable.escudo_boston_river)
    )
    val matchList = listOf<Match>(
        Match(
            0,
            0,
            1,
            MatchStatus.Pending,
            LocalDate.of(2022, 4, 26),
        ),
        Match(
            1,
            2,
            4,
            MatchStatus.Played,
            LocalDate.of(2022, 4, 26),
            goalsLocal = 1,
            goalsAway = 0
        ),
        Match(
            2,
            0,
            1,
            MatchStatus.Played,
            LocalDate.of(2022, 3, 23),
            3,
            1,
            listOf(
                MatchEvent(16, "De los Santos", MatchEventKind.Goal, MatchTeams.Local),
                MatchEvent(18, "De los Santos", MatchEventKind.Goal, MatchTeams.Local),
                MatchEvent(21, "De los Santos", MatchEventKind.Goal, MatchTeams.Local),
                MatchEvent(23, "Suarez", MatchEventKind.YellowCard, MatchTeams.Local),
                MatchEvent(24, "Fernandez", MatchEventKind.Goal, MatchTeams.Away),
                MatchEvent(24, "Fernandez", MatchEventKind.YellowCard, MatchTeams.Away),
                MatchEvent(78, "De los Santos", MatchEventKind.YellowCard, MatchTeams.Local),
                MatchEvent(79, "De los santos", MatchEventKind.RedCard, MatchTeams.Away)
            )
        ),
        Match(
            3,
            3,
            1,
            MatchStatus.Played,
            LocalDate.of(2022, 4, 25),
            2,
            0
        ),
        Match(
            4,
            3,
            4,
            MatchStatus.Played,
            LocalDate.of(2022, 4, 25),
            0,
            0
        )
    )
    val betList = listOf<Bet>(
        Bet(matchList.find { it.id == 0 }!!, BetStatus.Pending, 1, 0),
        Bet(matchList.find { it.id == 1 }!!, BetStatus.Done, 1, 0),
        Bet(matchList.find { it.id == 2 }!!, BetStatus.Done, 1, 3),
        Bet(matchList.find { it.id == 3 }!!, BetStatus.Done, 1, 0),
        Bet(matchList.find { it.id == 4 }!!, BetStatus.Pending)
    )
}