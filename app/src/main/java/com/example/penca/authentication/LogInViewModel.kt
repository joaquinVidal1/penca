package com.example.penca.authentication

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.penca.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(private val repository: MatchRepository) :
    ViewModel() {

    private val _showProgressBar = MutableLiveData<Boolean>(false)
    val showProgressBar: LiveData<Boolean>
        get() = _showProgressBar

    private val _logInResult = MutableLiveData<String?>()
    val logInResult: LiveData<String?>
        get() = _logInResult

    fun logIn(email: String, password: String) {
        _showProgressBar.value = true
        viewModelScope.launch { _logInResult.postValue(repository.logIn(email, password)) }
    }

    fun resultArrived(){
        _showProgressBar.value = false
    }

}