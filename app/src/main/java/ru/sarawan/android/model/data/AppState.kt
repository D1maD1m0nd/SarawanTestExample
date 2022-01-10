package ru.sarawan.android.model.data

sealed interface AppState<T> {
    data class Success<T>(val data: List<T>) : AppState<T>
    data class Error(val error: Throwable?) : AppState<Nothing>
    object Loading : AppState<Nothing>
    object Empty: AppState<Nothing>
}