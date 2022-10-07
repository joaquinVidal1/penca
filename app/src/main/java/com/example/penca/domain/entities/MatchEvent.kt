package com.example.penca.domain.entities

import com.example.penca.network.entities.NetworkMatchEvent

enum class MatchEventKind() {
    Goal,
    RedCard,
    YellowCard,
}

enum class MatchTeams {
    Home,
    Away
}

data class MatchEvent(
    val minute: Int,
    val playerName: String,
    val kind: MatchEventKind,
    val team: MatchTeams
) {
}
