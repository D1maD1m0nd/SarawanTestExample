package ru.sarawan.android.model.data

import ru.sarawan.android.model.data.product.Filter

data class MainScreenDataModel(
    val listOfElements: List<CardScreenDataModel>,
    val isLastPage: Boolean,
    val filters: List<Filter>? = null
)