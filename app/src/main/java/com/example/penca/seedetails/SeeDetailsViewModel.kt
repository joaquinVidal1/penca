package com.example.penca.seedetails

import androidx.lifecycle.ViewModel
import com.example.penca.domain.entities.Bet
import com.example.penca.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SeeDetailsViewModel @Inject constructor(private val repository: MatchRepository) :
    ViewModel() {
    fun getBetByMatchId(matchId: Int): Bet? {
        return repository.getBetByMatchId(matchId)
    }

}