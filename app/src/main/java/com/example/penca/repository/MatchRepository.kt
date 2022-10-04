package com.example.penca.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.penca.database.MatchDao
import com.example.penca.domain.entities.*
import com.example.penca.mainscreen.TeamKind
import com.example.penca.network.NetworkUser
import com.example.penca.network.UserNetwork
import kotlinx.coroutines.*
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchRepository @Inject constructor(private val matchDao: MatchDao) {

    private val _betList = MutableLiveData<List<Bet>>(listOf())
    val betList = MediatorLiveData<List<Bet>>()

    init {
        betList.addSource(matchDao.getMatches()){
            it.map { dbMatch ->
                val homeTeam = Team(dbMatch.homeTeamId, dbMatch.homeTeamName, dbMatch.homeTeamLogo)
                val awayTeam = Team(dbMatch.awayTeamId, dbMatch.awayTeamName, dbMatch.awayTeamLogo)
                val match = Match(
                    dbMatch.matchId,
                    homeTeam,
                    awayTeam,
                    dbMatch.date,
                    dbMatch.goalsLocal,
                    dbMatch.goalsAway,
                    null
                )
                Bet(match, )

            } ?: listOf()
        }
    }

    private val _networkError = MutableLiveData(false)
    val networkError: LiveData<Boolean>
        get() = _networkError

    private val loggedUser = MutableLiveData<NetworkUser>(null)

    private val _matchList = MutableLiveData<List<Match>>(listOf())

    private suspend fun getBetListFromMatchList(it: List<Match>): List<Bet> {
        return it.map { match ->
            withContext(Dispatchers.IO) {
                try {
                    val bet = UserNetwork.match.getMatchDetails(
                        UserNetwork.match.getUrlForMatchDetails(match.id),
                        loggedUser.value!!.token
                    )
                    val betStatus = if (bet.predictionStatus == "not_predicted") {
                        BetStatus.Pending
                    } else {
                        BetStatus.Done
                    }
                    Bet(match, betStatus, bet.homeTeamGoals, bet.awayTeamGoals)
                } catch (e: HttpException) {
                    Bet(match, BetStatus.Pending, null, null)
                }
            }
        }
    }

    suspend fun betScoreChanged(matchId: Int, newScore: Int, teamKind: TeamKind) {
        betList.value?.find { it.match.id == matchId }?.apply {
            if (teamKind == TeamKind.Home) {
                homeGoalsBet = newScore
                if (this.awayGoalsBet != null) {
                    placeBetOnApi(matchId, newScore, this.awayGoalsBet!!)
                }
            } else {
                awayGoalsBet = newScore
                if (this.homeGoalsBet != null) {
                    val homeGoals: Int = this.homeGoalsBet!!
                    placeBetOnApi(matchId, homeGoals, newScore)
                }
            }
        }
    }

    suspend fun getBetByMatchId(matchId: Int) =
        withContext(Dispatchers.IO) {
            UserNetwork.match.getMatchDetails(
                UserNetwork.match.getUrlForMatchDetails(matchId),
                loggedUser.value!!.token
            )
        }


    suspend fun logIn(email: String, password: String) =
        withContext(Dispatchers.IO) {
            try {
                val networkUser = UserNetwork.user.logIn(email, password)
                networkUser.token = "Bearer " + networkUser.token
                loggedUser.postValue(networkUser)
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

    private fun getMessage(errorResponse: String?) =
        errorResponse?.replace("{\"message\":\"", "")?.replace("\"}", "") ?: "error"

    suspend fun register(email: String, password: String) =
        withContext(Dispatchers.IO) {
            try {
                loggedUser.postValue(UserNetwork.user.register(email, password))
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

    suspend fun refreshMatches() {
        withContext(Dispatchers.IO) {
            _matchList.postValue(
                UserNetwork.match.getMatches(loggedUser.value!!.token).matches
                    .map { it.asDomainMatch() })
            _betList.postValue(getBetListFromMatchList(_matchList.value!!))
        }
    }

    private suspend fun placeBetOnApi(
        matchId: Int,
        homeTeamPrediction: Int,
        awayTeamPrediction: Int
    ) {
        withContext(Dispatchers.IO) {
            UserNetwork.match.placeBet(
                UserNetwork.match.getUrlForPlaceBet(matchId),
                loggedUser.value!!.token,
                homeTeamPrediction,
                awayTeamPrediction
            )

        }
    }

}
