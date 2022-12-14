package com.example.penca.authentication

import androidx.lifecycle.*
import com.example.penca.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val alreadyLogged = Transformations.map(authRepository.isUserLogged) { it }

    private val _showProgressBar = MutableLiveData(false)
    val showProgressBar: LiveData<Boolean>
        get() = _showProgressBar

    private val _logInResult = MutableLiveData<String?>()
    val logInResult: LiveData<String?>
        get() = _logInResult

    fun logIn(email: String, password: String) {
        _showProgressBar.value = true
        // TODO creo que no es necesario usar postValue
        viewModelScope.launch { _logInResult.postValue(authRepository.logIn(email, password)) }
    }

    fun resultArrived() {
        _showProgressBar.value = false
    }

}