package com.example.penca.seedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.penca.network.SeeDetailsBet
import com.example.penca.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeeDetailsViewModel @Inject constructor(private val repository: MatchRepository) :
    ViewModel() {

    private val _bet = MutableLiveData<SeeDetailsBet>()
    val bet: LiveData<SeeDetailsBet>
        get() = _bet

    private val _loadingContents = MutableLiveData(false)
    val loadingContents: LiveData<Boolean>
        get() = _loadingContents


    fun getBetByMatchId(matchId: Int) {
        _loadingContents.value = true
        viewModelScope.launch {
            _bet.postValue(repository.getBetDetails(matchId))
            _loadingContents.postValue(false)
        }

    }

}