package com.example.penca.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.penca.R
import com.example.penca.domain.entities.*
import com.example.penca.mainscreen.TeamKind
import com.example.penca.network.NetworkUser
import com.example.penca.network.UserNetwork
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.time.LocalDate
import javax.inject.Inject

class MatchRepository @Inject constructor() {

    private val _networkError = MutableLiveData(false)
    val networkError: LiveData<Boolean>
        get() = _networkError

    private lateinit var loggedUser: NetworkUser

    private val _teamsList = MutableLiveData(
        listOf(
            Team(0, "Peñarol", R.drawable.escudo_penarol),
            Team(1, "Nacional", R.drawable.escudo_nacional),
            Team(2, "Maldonado", R.drawable.escudo_maldonado),
            Team(3, "Fénix", R.drawable.escudo_fenix),
            Team(4, "Boston River", R.drawable.escudo_boston_river)
        )
    )
    private val matchList = listOf(
        Match(
            0,
            _teamsList.value?.find { it.id == 0 }!!,
            _teamsList.value?.find { it.id == 1 }!!,
            LocalDate.of(2022, 4, 26),
        ),
        Match(
            1,
            _teamsList.value?.find { it.id == 2 }!!,
            _teamsList.value?.find { it.id == 4 }!!,
            LocalDate.of(2022, 4, 26),
            goalsLocal = 1,
            goalsAway = 0
        ),
        Match(
            2,
            _teamsList.value?.find { it.id == 0 }!!,
            _teamsList.value?.find { it.id == 1 }!!,
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
            _teamsList.value?.find { it.id == 3 }!!,
            _teamsList.value?.find { it.id == 1 }!!,
            LocalDate.of(2022, 4, 25),
            2,
            0
        ),
        Match(
            4,
            _teamsList.value?.find { it.id == 3 }!!,
            _teamsList.value?.find { it.id == 4 }!!,
            LocalDate.of(2022, 4, 25),
            0,
            0
        )
    )
    private val _betList = MutableLiveData(
        listOf(
            Bet(matchList.find { it.id == 0 }!!, BetStatus.Pending, 1, 0),
            Bet(matchList.find { it.id == 1 }!!, BetStatus.Done, 1, 0),
            Bet(matchList.find { it.id == 2 }!!, BetStatus.Done, 1, 3),
            Bet(matchList.find { it.id == 3 }!!, BetStatus.Done, 1, 0),
            Bet(matchList.find { it.id == 4 }!!, BetStatus.Pending)
        )
    )
    val betList: LiveData<List<Bet>>
        get() = _betList

    fun betScoreChanged(matchId: Int, newScore: Int, teamKind: TeamKind) {
        betList.value?.find { it.match.id == matchId }?.apply {
            if (teamKind == TeamKind.Local) {
                homeGoalsBet = newScore
            } else {
                awayGoalsBet = newScore
            }
        }
    }

    fun getBetByMatchId(matchId: Int): Bet? {
        return betList.value?.find { bet -> bet.match.id == matchId }
    }

    suspend fun logIn(email: String, password: String) =
        withContext(Dispatchers.IO) {
            try {
                loggedUser = UserNetwork.user.logIn(email, password)
                null
            } catch (e: HttpException) {
                val errorResponse = e.response()!!.errorBody()?.charStream()?.readText()
                Log.e(
                    "http exception",
                    errorResponse.toString()
                )
                getMessage(errorResponse)
            }
        }

    private fun getMessage(errorResponse: String?): String {
        return errorResponse?.replace("{\"message\":\"", "")?.replace("\"}", "") ?: "error"

    }

    suspend fun register(email: String, password: String) =
        withContext(Dispatchers.IO) {
            try {
                loggedUser = UserNetwork.user.register(email, password)
                null
            } catch (e: HttpException) {
                val errorResponse = e.response()!!.errorBody()?.charStream()?.readText()
                Log.e(
                    "http exception",
                    errorResponse.toString()
                )
                getMessage(errorResponse)
            }
        }


}
