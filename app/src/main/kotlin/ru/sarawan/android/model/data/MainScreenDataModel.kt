package ru.sarawan.android.model.data

data class MainScreenDataModel(
    val listOfElements: List<CardScreenDataModel>,
    val isLastPage: Boolean,
    val filters: List<Filter>? = null
)