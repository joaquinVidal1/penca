package com.example.penca.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.penca.database.DBMatch
import com.example.penca.database.MatchDao
import com.example.penca.domain.entities.*
import com.example.penca.mainscreen.BetFilter
import com.example.penca.network.*
import com.example.penca.network.entities.BetBody
import com.example.penca.network.entities.SeeDetailsBet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.lang.Exception
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchRepository @Inject constructor(
    private val matchDao: MatchDao,
    private val matchApi: MatchService
) {
    private val _betList = MutableLiveData<List<Bet>>(listOf())
    val betList = MediatorLiveData<List<Bet>>()
    private val databaseMatches: LiveData<List<Bet>> =
        Transformations.map(matchDao.getMatches()) {
            it.map { dbMatch -> dbMatch.asBet() }
        }

    init {
        betList.addSource(databaseMatches) {
            betList.value = it
        }
    }

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

    suspend fun betScoreChanged(matchId: Int, homeScore: Int?, awayScore: Int?) {
        betList.value?.find { it.match.id == matchId }?.apply {
            this.homeGoalsBet = homeScore
            this.awayGoalsBet = awayScore
            if (homeScore != null && awayScore != null) {
                placeBetOnApi(matchId, homeScore, awayScore)
                matchDao.insertMatch(this.asDBMatch())
            }
        }
    }

    private suspend fun getBetByMatchIdFromApi(matchId: Int): SeeDetailsBet? {
        return try {
            matchApi.getMatchDetails(
                matchId,
            )
        } catch (e: Exception) {
            null
        }
    }

//    suspend fun refreshMatches() {
//        withContext(Dispatchers.IO) {
//            _matchList.postValue(
//                UserNetwork.match.getMatches(loggedUser.token).matches
//                    .map { it.asDomainMatch() })
//            _betList.postValue(getBetListFromMatchList(_matchList.value!!))
//        }
//    }

    suspend fun refreshMatches(filter: BetFilter = BetFilter.SeeAll, query: String = "") {
        try {
            val matches = matchApi.getMatches()
            withContext(Dispatchers.IO) {
                matchDao.emptyAndInsert(matches.matches.map {
                    it.asDBMatch()
                })
            }
        } catch (networkError: HttpException) {
            _networkError.value = true
        }

    }

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

    private suspend fun placeBetOnApi(
        matchId: Int,
        homeTeamPrediction: Int,
        awayTeamPrediction: Int
    ) {
        withContext(Dispatchers.IO) {
            matchApi.placeBet(
                matchId,
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

}
