package com.example.penca.repository

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.penca.database.MatchDao
import com.example.penca.domain.entities.*
import com.example.penca.network.*
import com.example.penca.utils.PreferenceHelper
import kotlinx.coroutines.*
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchRepository @Inject constructor(
    private val matchDao: MatchDao,
    private val preferences: SharedPreferences,
    private val preferenceHelper: PreferenceHelper,
) {
    private val _betList = MutableLiveData<List<Bet>>(listOf())
    val betList = MediatorLiveData<List<Bet>>()
    private lateinit var loggedUser: NetworkUser

//    init {
//        betList.addSource(matchDao.getMatches()) { list ->
//            list.map { dbMatch ->
//                val homeTeam = Team(dbMatch.homeTeamId, dbMatch.homeTeamName, dbMatch.homeTeamLogo)
//                val awayTeam = Team(dbMatch.awayTeamId, dbMatch.awayTeamName, dbMatch.awayTeamLogo)
//                if (dbMatch.date.isBefore(LocalDate.now())) {
//                    val match = Match(
//                        dbMatch.matchId,
//                        homeTeam,
//                        awayTeam,
//                        dbMatch.date,
//                        null,
//                        null,
//                        null
//                    )
//                    val betStatus = if (dbMatch.goalsAway == null || dbMatch.goalsHome == null) {
//                        BetStatus.Pending
//                    } else {
//                        BetStatus.Done
//                    }
//                    Bet(match, betStatus, dbMatch.goalsHome, dbMatch.goalsAway)
//                } else {
//                    val match = Match(
//                        dbMatch.matchId,
//                        homeTeam,
//                        awayTeam,
//                        dbMatch.date,
//                        dbMatch.goalsHome,
//                        dbMatch.goalsAway,
//                        null
//                    )
//                    Bet(match)
//                }
//                val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
//                scope.launch {
//                    val prediction = getPredictionForMatch(dbMatch)
//                    if (prediction == null) {
//                        Bet(match)
//                    } else {
//                        Bet(match, BetStatus.Done, prediction.first, prediction.second)
//                    }
//
//                }
//            }
//        }
//    }

    private val _networkError = MutableLiveData(false)
    val networkError: LiveData<Boolean>
        get() = _networkError

    private val _matchList = MutableLiveData<List<Match>>(listOf())

//    private suspend fun getBetListFromMatchList(it: List<Match>): List<Bet> {
//        return it.map { match ->
//            withContext(Dispatchers.IO) {
//                try {
//                    val bet = UserNetwork.match.getMatchDetails(
//                        match.id,
//                        loggedUser.token
//                    )
//                    val betStatus = if (bet.predictionStatus == "not_predicted") {
//                        BetStatus.Pending
//                    } else {
//                        BetStatus.Done
//                    }
//                    Bet(match, betStatus, bet.homeTeamGoals, bet.awayTeamGoals)
//                } catch (e: HttpException) {
//                    Bet(match, BetStatus.Pending, null, null)
//                }
//            }
//        }
//    }

//    suspend fun betScoreChanged(matchId: Int, homeScore: Int?, awayScore: Int?) {
//        betList.value?.find { it.match.id == matchId }?.apply {
//            this.homeGoalsBet = homeScore
//            this.awayGoalsBet = awayScore
//            if (homeScore != null && awayScore != null)
//                placeBetOnApi(matchId, homeScore, awayScore)
//        }
//    }

//    private suspend fun getBetByMatchIdFromApi(matchId: Int) =
//        withContext(Dispatchers.IO) {
//            try {
//                UserNetwork.match.getMatchDetails(
//                    matchId,
//                    loggedUser.token
//                )
//            } catch (e: HttpException) {
//                null
//            }
//        }

//    suspend fun refreshMatches() {
//        withContext(Dispatchers.IO) {
//            _matchList.postValue(
//                UserNetwork.match.getMatches(loggedUser.token).matches
//                    .map { it.asDomainMatch() })
//            _betList.postValue(getBetListFromMatchList(_matchList.value!!))
//        }
//    }

//    private suspend fun getPredictionForMatch(match: DBMatch): Pair<Int, Int>? {
//        val result: Pair<Int, Int>?
//        if (match.date >= LocalDate.now()) {
//            val prediction =
//                predictionList.value?.find { prediction -> prediction.matchId == match.matchId && prediction.userId == loggedUser.userId }
//            result = if (prediction != null) Pair(
//                prediction.homeGoalsBet,
//                prediction.awayGoalsBet
//            ) else null
//        } else {
//            withContext(Dispatchers.IO) {
//                val betDetails = getBetByMatchIdFromApi(match.matchId)
//                betDao.deleteBet(loggedUser.userId, match.matchId)
//                result = if (betDetails != null) {
//                    Pair(
//                        betDetails.homeTeamPrediction!!,
//                        betDetails.awayTeamPrediction!!
//                    )
//                } else null
//            }
//        }
//        return result
//    }

//    private suspend fun placeBetOnApi(
//        matchId: Int,
//        homeTeamPrediction: Int,
//        awayTeamPrediction: Int
//    ) {
//        withContext(Dispatchers.IO) {
//            UserNetwork.match.placeBet(
//                matchId,
//                loggedUser.token,
//                BetBody(
//                    homeTeamPrediction,
//                    awayTeamPrediction
//                )
//            )
//
//        }
//    }

//    suspend fun getBetDetails(matchId: Int): SeeDetailsBet? {
//        return getBetByMatchIdFromApi(matchId)
//    }

}
