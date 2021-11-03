package com.example.sarawan.model.data

sealed class AppState {

    data class Success<T>(val data: List<T>?) : AppState()
    data class Error(val error: Throwable?) : AppState()
    object Loading : AppState()
}