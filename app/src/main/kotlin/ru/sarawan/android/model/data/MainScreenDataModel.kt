package ru.sarawan.android.model.data

data class MainScreenDataModel(
    val listOfElements: List<CardScreenDataModel>,
    val maxElement: Int,
    val filters: List<Filter>? = null
)