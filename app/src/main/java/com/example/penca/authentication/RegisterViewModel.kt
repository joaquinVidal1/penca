package com.example.penca.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.penca.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<String>()
    val registerResult: LiveData<String?>
        get() = _registerResult

    private val _showProgressBar = MutableLiveData(false)
    val showProgressBar: LiveData<Boolean>
        get() = _showProgressBar

    fun register(email: String, password: String) {
        _showProgressBar.value = true
        viewModelScope.launch { _registerResult.postValue(repository.register(email, password)) }
    }

    fun resultArrived() {
        _showProgressBar.value = false
    }


}