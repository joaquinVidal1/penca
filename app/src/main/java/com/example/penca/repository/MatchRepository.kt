package com.example.penca.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.penca.database.BetDao
import com.example.penca.database.DBBet
import com.example.penca.database.DBMatch
import com.example.penca.database.MatchDao
import com.example.penca.domain.entities.*
import com.example.penca.mainscreen.TeamKind
import com.example.penca.network.BetBody
import com.example.penca.network.NetworkUser
import com.example.penca.network.SeeDetailsBet
import com.example.penca.network.UserNetwork
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchRepository @Inject constructor(
    private val matchDao: MatchDao,
    private val betDao: BetDao
) {

    private val _betList = MutableLiveData<List<Bet>>(listOf())
    val betList = MediatorLiveData<List<Bet>>()
    private lateinit var loggedUser: NetworkUser
    private lateinit var predictionList: LiveData<List<DBBet>>

    init {
        betList.addSource(matchDao.getMatches()) { list ->
            list.map { dbMatch ->
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
                val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
                scope.launch {
                    val prediction = getPredictionForMatch(dbMatch)
                    if (prediction == null) {
                        Bet(match)
                    } else {
                        Bet(match, BetStatus.Done, prediction.first, prediction.second)
                    }

                }
            }
        }
    }

    private val _networkError = MutableLiveData(false)
    val networkError: LiveData<Boolean>
        get() = _networkError

    private val _matchList = MutableLiveData<List<Match>>(listOf())

    private suspend fun getBetListFromMatchList(it: List<Match>): List<Bet> {
        return it.map { match ->
            withContext(Dispatchers.IO) {
                try {
                    val bet = UserNetwork.match.getMatchDetails(
                        UserNetwork.match.getUrlForMatchDetails(match.id),
                        loggedUser.token
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
                    betDao.insertBet(
                        DBBet(
                            matchId,
                            loggedUser.userId,
                            homeGoalsBet!!,
                            awayGoalsBet!!
                        )
                    )
                    placeBetOnApi(matchId, newScore, this.awayGoalsBet!!)
                } else {
                    betDao.insertBet(DBBet(matchId, loggedUser.userId, homeGoalsBet!!, -1))
                }
            } else {
                awayGoalsBet = newScore
                if (this.homeGoalsBet != null) {
                    betDao.insertBet(
                        DBBet(
                            matchId,
                            loggedUser.userId,
                            homeGoalsBet!!,
                            awayGoalsBet!!
                        )
                    )
                    val homeGoals: Int = this.homeGoalsBet!!
                    placeBetOnApi(matchId, homeGoals, newScore)
                } else {
                    betDao.insertBet(DBBet(matchId, loggedUser.userId, -1, awayGoalsBet!!))
                }
            }
        }
    }

    private suspend fun getBetByMatchIdFromApi(matchId: Int) =
        withContext(Dispatchers.IO) {
            try {
                UserNetwork.match.getMatchDetails(
                    UserNetwork.match.getUrlForMatchDetails(matchId),
                    loggedUser.token
                )
            } catch (e: HttpException) {
                null
            }
        }


    suspend fun logIn(email: String, password: String) =
            try {
                val networkUser = UserNetwork.user.logIn(email, password)
                networkUser.token = "Bearer " + networkUser.token
                loggedUser = networkUser
                refreshFromDatabase(networkUser.userId)
                null
            } catch (e: HttpException) {
                val errorResponse = e.response()!!.errorBody()?.charStream()?.readText()
                Log.e(
                    "http exception",
                    errorResponse.toString()
                )
                getMessage(errorResponse)
            }


    private fun getMessage(errorResponse: String?) =
        errorResponse?.replace("{\"message\":\"", "")?.replace("\"}", "") ?: "error"

    suspend fun register(email: String, password: String) =
            try {
                val networkUser = UserNetwork.user.register(email, password)
                networkUser.token = "Bearer " + networkUser.token
                loggedUser = networkUser
                refreshFromDatabase(networkUser.userId)
                null
            } catch (e: HttpException) {
                val errorResponse = e.response()!!.errorBody()?.charStream()?.readText()
                Log.e(
                    "http exception",
                    errorResponse.toString()
                )
                getMessage(errorResponse)
            }

    suspend fun refreshMatches() {
        withContext(Dispatchers.IO) {
            _matchList.postValue(
                UserNetwork.match.getMatches(loggedUser.token).matches
                    .map { it.asDomainMatch() })
            _betList.postValue(getBetListFromMatchList(_matchList.value!!))
        }
    }

    private suspend fun getPredictionForMatch(match: DBMatch): Pair<Int, Int>? {
        val result :Pair<Int, Int>?
        if (match.date >= LocalDate.now()) {
            val prediction =
                predictionList.value?.find { prediction -> prediction.matchId == match.matchId && prediction.userId == loggedUser.userId }
            result = if (prediction != null) Pair(prediction.homeGoalsBet, prediction.awayGoalsBet) else null
        } else {
            withContext(Dispatchers.IO) {
                val betDetails = getBetByMatchIdFromApi(match.matchId)
                betDao.deleteBet(loggedUser.userId, match.matchId)
                result = if (betDetails != null) {
                    Pair(
                        betDetails.homeTeamPrediction!!,
                        betDetails.awayTeamPrediction!!
                    )
                }else null
            }
        }
        return result
    }

    private suspend fun placeBetOnApi(
        matchId: Int,
        homeTeamPrediction: Int,
        awayTeamPrediction: Int
    ) {
        withContext(Dispatchers.IO) {
            UserNetwork.match.placeBet(
                UserNetwork.match.getUrlForPlaceBet(matchId),
                loggedUser.token,
                BetBody(
                    homeTeamPrediction,
                    awayTeamPrediction
                )
            )

        }
    }

    suspend fun getBetDetails(matchId: Int): SeeDetailsBet? {
        return getBetByMatchIdFromApi(matchId)
    }

    private fun refreshFromDatabase(userId: Int){
        predictionList = Transformations.map(betDao.getBetsForUser(userId)) { it }
        betDao.deleteOtherUsersBets(userId)
    }
}
