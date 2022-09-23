package com.example.penca.mainscreen

import android.util.Log
import androidx.lifecycle.*
import com.example.penca.domain.entities.Bet
import com.example.penca.domain.entities.Header
import com.example.penca.domain.entities.ScreenItem
import com.example.penca.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

enum class TeamKind {
    Local,
    Away
}

@HiltViewModel
class MainScreenViewModel @Inject constructor(private val repository: MatchRepository) :
    ViewModel() {

    private val _query = MutableLiveData("")
    val bets = MediatorLiveData<List<ScreenItem>>()

    init {
        bets.addSource(repository.betList) {
            bets.value = getScreenList(it)
        }
        bets.addSource(_query) { query ->
            bets.value = repository.betList.value?.filter {
                it.match.homeTeam.name.lowercase()
                    .contains(query) || it.match.awayTeam.name.lowercase().contains(query)
            }
                ?.let { getScreenList(it) }
        }
    }

    private fun getScreenList(list: List<Bet>): List<ScreenItem> {
        val screenList = mutableListOf<ScreenItem>()
        list.sortedByDescending { it.match.date }
            .groupBy { it.match.date }.entries.forEach { (date, bets) ->
            screenList.add(
                ScreenItem.ScreenHeader(
                    Header(Header.getHeaderText(date))
                )
            )
            screenList.addAll(bets.map { bet -> ScreenItem.ScreenBet(bet) })
        }
        return screenList
    }

    fun getAllItems() {
        Log.i("ViewModel", "update items")
    }

    fun onQueryChanged(query: String) {
        _query.value = query
    }

    fun betScoreChanged(matchId: Int, newScore: Int, teamKind: TeamKind) {
        repository.betScoreChanged(matchId, newScore, teamKind)
    }

}