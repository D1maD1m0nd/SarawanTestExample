package com.example.sarawan.model.data

sealed class AppState {

    data class Success(val data: List<DataModel>) : AppState()
    data class Error(val error: Throwable?) : AppState()
    object Loading : AppState()
}