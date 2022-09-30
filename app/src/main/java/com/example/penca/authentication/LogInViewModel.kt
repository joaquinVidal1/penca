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

    private val _logInResult = MutableLiveData<Boolean?>(null)
    val logInResult: LiveData<Boolean?>
        get() = _logInResult

    fun logIn(email: String, password: String) =
        viewModelScope.async { _logInResult.postValue(repository.logIn(email, password)) }

}