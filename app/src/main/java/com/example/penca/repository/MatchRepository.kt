package com.example.penca.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.penca.database.MatchDao
import com.example.penca.domain.entities.*
import com.example.penca.mainscreen.BetFilter
import com.example.penca.mainscreen.BetFilter.Companion.getStringForApi
import com.example.penca.network.*
import com.example.penca.network.entities.BetAnswer
import com.example.penca.network.entities.BetBody
import com.example.penca.network.entities.SeeDetailsBet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchRepository @Inject constructor(
    private val matchDao: MatchDao,
    private val matchApi: MatchService
) {
    val betList = MediatorLiveData<List<Bet>>()

    private val _noMoreBets = MutableLiveData(false)
    val noMoreBets: LiveData<Boolean>
        get() = _noMoreBets

    private val databaseMatches: LiveData<List<Bet>> =
        Transformations.map(matchDao.getMatches()) {
            it.map { dbMatch -> dbMatch.asBet() }
        }
    private val _extraBets = MutableLiveData<List<Bet>>(listOf())

    init {
        betList.addSource(databaseMatches) {
            betList.value =
                it.plus(betList.value ?: listOf()).distinctBy { bet -> bet.match.id }.toList()
        }
        betList.addSource(_extraBets) {
            betList.value =
                it?.plus(betList.value ?: listOf())?.distinctBy { bet -> bet.match.id } ?: listOf()
        }
    }

    private val _networkError = MutableLiveData(false)
    val networkError: LiveData<Boolean>
        get() = _networkError

    suspend fun betScoreChanged(matchId: Int, homeScore: Int?, awayScore: Int?) {
        val bet = betList.value?.find { it.match.id == matchId }
        if (bet != null) {
            bet.homeGoalsBet = homeScore
            bet.awayGoalsBet = awayScore
            withContext(Dispatchers.IO) {
                matchDao.insertMatch(bet.asDBMatch())
            }
            if (homeScore != null && awayScore != null) {
                //TODO manejar cuando backend responde mal
                placeBetOnApi(matchId, homeScore, awayScore)
            }
        }
//            this.homeGoalsBet = homeScore
//            this.awayGoalsBet = awayScore
//            if (homeScore != null && awayScore != null) {
//                placeBetOnApi(matchId, homeScore, awayScore)
//                val bet = this
//                withContext(Dispatchers.IO) {
//                    matchDao.insertMatch(bet.asDBMatch())
//                }
//            }
//        val updatedDBList = databaseMatches.apply {
//            this.value?.find { it.match.id == matchId }
//                .let {
//                    it?.homeGoalsBet = homeScore
//                    it?.awayGoalsBet = awayScore
//                }
//        }
//        withContext(Dispatchers.IO) {
//            matchDao.emptyAndInsert(updatedDBList.value!!.map { it.asDBMatch() })
//        }
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

    suspend fun refreshMatches() {
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

    private suspend fun placeBetOnApi(
        matchId: Int,
        homeTeamPrediction: Int,
        awayTeamPrediction: Int
    ): BetAnswer {
        return matchApi.placeBet(
            matchId,
            BetBody(
                homeTeamPrediction,
                awayTeamPrediction
            )
        )

    }

    suspend fun getBetDetails(matchId: Int): SeeDetailsBet? {
        return getBetByMatchIdFromApi(matchId)
    }

    suspend fun loadMoreBets(
        numberOfPageToLoad: Int,
        teamName: String? = null,
        betFilter: BetFilter = BetFilter.SeeAll
    ) {
        val obtainedMatches = matchApi.getMatches(
            numberOfPageToLoad,
            20,
            teamName,
            getStringForApi(betFilter)
        ).matches
        if (obtainedMatches.isEmpty()) {
            _noMoreBets.value = true
        }
        _extraBets.value = betList.value?.plus(obtainedMatches.map { it.asBet() })
    }

    suspend fun getBanners(): List<String>{
        val response = matchApi.getBannersUrl()
        return response.bannersUrls
    }

}
