package com.example.penca.repository

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SeeDetailsViewModel @Inject constructor(private val repository: MatchRepository) : ViewModel() {
    
}