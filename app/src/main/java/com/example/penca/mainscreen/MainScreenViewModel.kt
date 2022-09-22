package com.example.penca.mainscreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.penca.domain.entities.Bet
import com.example.penca.domain.entities.Header
import com.example.penca.domain.entities.ScreenItem
import com.example.penca.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(private val repository: MatchRepository) :
    ViewModel() {
    private val _bets = Transformations.map(repository.betList) { getScreenList(it) }

    val bets: LiveData<List<ScreenItem>>
        get() = _bets

    private fun getScreenList(list: List<Bet>): List<ScreenItem> {
        val screenList = mutableListOf<ScreenItem>()
        list.sortedByDescending { it.match.date }.groupBy { it.match.date }.entries.forEach { (date, bets) ->
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

}