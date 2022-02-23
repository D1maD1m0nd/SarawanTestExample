package ru.sarawan.android.model.data

import ru.sarawan.android.utils.constants.TypeCase

sealed interface AppState<T> {
    data class Success<T>(val data: List<T>, val case: TypeCase = TypeCase.DEFAULT) : AppState<T>
    data class Error(val error: Throwable?) : AppState<Nothing>
    object Loading : AppState<Nothing>
    object Empty : AppState<Nothing>
}