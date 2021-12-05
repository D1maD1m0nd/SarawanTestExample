package com.example.sarawan.model.data

import com.example.sarawan.framework.ui.base.mainCatalog.CardType
import com.squareup.moshi.Json

data class CategoryDataModel(
    @field:Json(name = "id") val id: Int? = null,
    @field:Json(name = "name") val name: String? = null
)

fun CategoryDataModel.toMainScreenDataModel() : MainScreenDataModel {
    return MainScreenDataModel(
        id = id?.toLong(),
        itemDescription = name,
        cardType = CardType.STRING.type
    )
}