package com.example.penca.authentication

import androidx.lifecycle.ViewModel
import com.example.penca.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val repository: MatchRepository) :
    ViewModel() {

}