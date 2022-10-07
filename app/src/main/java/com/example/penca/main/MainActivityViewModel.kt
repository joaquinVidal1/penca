package com.example.penca.main

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.penca.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val logOut = Transformations.map(authRepository.shouldLogOut) { it }
}