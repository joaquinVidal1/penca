package com.example.penca.mainscreen

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

@HiltViewModel
class MainScreenViewModel @Inject constructor(private val repository: MatchRepository) :
    ViewModel() {

    private val _banners = MutableLiveData<List<String>>()
    val banners: LiveData<List<String>>
        get() = _banners

    val noMoreBets = Transformations.map(repository.noMoreBets) { it }
    private var numberOfPageLoaded = 1
    private var numberOfPageLoadedWithQueryOrFilter = 1
    private val _query = MutableLiveData("")
    val bets = MediatorLiveData<List<ScreenItem>>()
    private val nonFilteredBets = Transformations.map(repository.betList) {
        it.sortedByDescending { bet -> bet.match.date }
    }
    private val _filter = MutableLiveData(BetFilter.SeeAll)
    val filter: LiveData<BetFilter>
        get() = _filter

    private val _betChanged = MutableLiveData(-1)
    val betChanged: LiveData<Int>
        get() = _betChanged

    private val _loadingContents = MutableLiveData(false)
    val loadingContents: LiveData<Boolean>
        get() = _loadingContents

    private val _loadingMoreBets = MutableLiveData(false)
    val loadingMoreBets: LiveData<Boolean>
        get() = _loadingMoreBets

    init {
        viewModelScope.launch {
            _banners.value = repository.getBanners()
        }

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
            numberOfPageLoadedWithQueryOrFilter = 1
            if (bets.value?.size!! < 5 && !_loadingContents.value!!) {
                loadMoreBets()
            }
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
            numberOfPageLoadedWithQueryOrFilter = 1
            if (bets.value?.size!! < 5 && !_loadingContents.value!!) {
                loadMoreBets()
            }
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
                        bet.match.status == MatchStatus.Played && bet.status == BetStatus.NotDone
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

    fun betScoreChanged(matchId: Int, newScore: Int, teamKind: TeamKind) {
        bets.value?.find { if (it is ScreenItem.ScreenBet) it.bet.match.id == matchId else false }
            .let {
                it as ScreenItem.ScreenBet
                if (teamKind == TeamKind.Home) {
                    it.bet.homeGoalsBet = newScore
                } else {
                    it.bet.awayGoalsBet = newScore
                }
                _betChanged.value = matchId
                viewModelScope.launch {
                    repository.betScoreChanged(
                        it.bet.match.id,
                        it.bet.homeGoalsBet,
                        it.bet.awayGoalsBet
                    )
                }
            }
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

    fun loadMoreBets() {
        val teamName = if (_query.value == null || _query.value == "") null else _query.value
        val filter = _filter.value ?: BetFilter.SeeAll
        _loadingMoreBets.value = true
        val pageToLoad: Int
        if ((filter == BetFilter.SeeAll) && (_query.value == "")) {
            numberOfPageLoadedWithQueryOrFilter = 1
            numberOfPageLoaded += 1
            pageToLoad = numberOfPageLoaded
        } else {
            pageToLoad = numberOfPageLoadedWithQueryOrFilter
            numberOfPageLoadedWithQueryOrFilter += 1
        }
        viewModelScope.launch {
            repository.loadMoreBets(pageToLoad, teamName, filter)
            _loadingMoreBets.postValue(false)
        }
    }

}