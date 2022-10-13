package com.example.penca.main

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.penca.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    authRepository: AuthRepository
) : ViewModel() {
    // TODO esto creo que ya lo hemos revisado en instancias anteriores. Esta transformacion no hace nada. Se puede hacer val logOut = authRepository.shouldLogOut
    val logOut = Transformations.map(authRepository.shouldLogOut) { it }
}