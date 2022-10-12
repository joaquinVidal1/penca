package com.example.penca.seedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.penca.network.entities.SeeDetailsBet
import com.example.penca.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeeDetailsViewModel @Inject constructor(private val repository: MatchRepository) :
    ViewModel() {

    lateinit var bet: SeeDetailsBet

    private val _networkError = MutableLiveData<Boolean>(false)
    val networkError: LiveData<Boolean>
        get() = _networkError


    private val _loadingContents = MutableLiveData(false)
    val loadingContents: LiveData<Boolean>
        get() = _loadingContents


    fun getBetByMatchId(matchId: Int) {
        _loadingContents.value = true
        viewModelScope.launch {
            val obtainedBet = repository.getBetDetails(matchId)
            if (obtainedBet == null) {
                _networkError.value = true
            } else {
                bet = obtainedBet
                _loadingContents.postValue(false)
            }
        }

    }

}