package com.example.penca.mainscreen

import android.util.Log
import androidx.lifecycle.*
import com.example.penca.domain.entities.*
import com.example.penca.mainscreen.BetFilter.Companion.getBetStatusResultAndMatchStatus
import com.example.penca.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TeamKind {
    Home,
    Away
}

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
                SeePlayedWithNoResult -> Triple(BetStatus.Pending, null, MatchStatus.Played)
                SeeAll -> Triple(null, null, null)
            }
        }
    }
}

@HiltViewModel
class MainScreenViewModel @Inject constructor(private val repository: MatchRepository) :
    ViewModel() {

    private val _query = MutableLiveData("")
    val bets = MediatorLiveData<List<ScreenItem>>()
    private val nonFilteredBets = Transformations.map(repository.betList) { it }
    private val _filter = MutableLiveData(BetFilter.SeeAll)
    val filter: LiveData<BetFilter>
        get() = _filter

    private val _loadingContents = MutableLiveData(false)
    val loadingContents: LiveData<Boolean>
        get() = _loadingContents

    init {
        bets.addSource(_filter) { filter ->
            bets.value = getScreenList(nonFilteredBets.value?.let { betList ->
                filterBySelection(betList, filter).let { it1 ->
                    _query.value?.let { it2 ->
                        filterByQuery(
                            it1, it2
                        )
                    }
                }
            })
        }
        bets.addSource(nonFilteredBets) { betList ->
            bets.value = getScreenList(betList.let { it1 ->
                _filter.value?.let {
                    filterBySelection(it1, it).let { it1 ->
                        _query.value?.let { it2 ->
                            filterByQuery(
                                it1, it2
                            )
                        }
                    }
                }
            })
        }
        bets.addSource(_query) { query ->
            bets.value = getScreenList(nonFilteredBets.value?.let {
                _filter.value?.let { it1 ->
                    filterBySelection(
                        it,
                        it1
                    )
                }
            }
                ?.let { filterByQuery(it, query) })
        }
    }

    private fun filterByQuery(betsToFilter: List<Bet>, query: String): List<Bet> {
        return betsToFilter.filter {
            it.match.homeTeam.name.lowercase()
                .contains(query) || it.match.awayTeam.name.lowercase().contains(query)
        }
    }

    private fun filterBySelection(betsToFilter: List<Bet>, filter: BetFilter): List<Bet> {
        return betsToFilter.filter { bet ->
            val betFilter = getBetStatusResultAndMatchStatus(filter)
            if (betFilter == Triple(null, null, null)) {
                true
            } else {
                if (betFilter.first == null) {
                    bet.match.status == MatchStatus.Pending
                } else {
                    if (betFilter.second == null) {
                        bet.match.status == MatchStatus.Played && bet.status == BetStatus.Pending
                    } else {
                        Triple(bet.status, bet.result, bet.match.status) == betFilter
                    }
                }
            }
        }
    }

    private fun getScreenList(list: List<Bet>?): List<ScreenItem> {
        val screenList = mutableListOf<ScreenItem>()
        list?.sortedByDescending { it.match.date }
            ?.groupBy { it.match.date }?.entries?.forEach { (date, bets) ->
                screenList.add(
                    ScreenItem.ScreenHeader(
                        Header(Header.getHeaderText(date))
                    )
                )
                screenList.addAll(bets.map { bet -> ScreenItem.ScreenBet(bet) })
            }
        return screenList
    }

    fun onQueryChanged(query: String) {
        _query.value = query
    }

    fun betScoreChanged(bet: Bet, newScore: Int, teamKind: TeamKind) {
        if (teamKind == TeamKind.Home){
            bet.homeGoalsBet = newScore
        }else{
            bet.awayGoalsBet = newScore
        }
//        viewModelScope.launch {
//            repository.betScoreChanged(bet.match.id, bet.homeGoalsBet, bet.awayGoalsBet)
//        }
    }

    fun onFilterChanged(filter: BetFilter) {
        _filter.value = filter
    }

    fun refreshMatches() {
        _loadingContents.value = true
        viewModelScope.launch {
            repository.refreshMatches()
            _loadingContents.postValue(false)
        }
    }

}