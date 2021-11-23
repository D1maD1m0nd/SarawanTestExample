package com.example.sarawan.model.data

sealed interface AppState<T> {
    data class BasketSuccess<T>(val data : Basket) : AppState<T>
    data class Success<T>(val data: List<T>) : AppState<T>
    data class Error(val error: Throwable?) : AppState<Nothing>
    object Loading : AppState<Nothing>
}