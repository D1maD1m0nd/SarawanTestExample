package ru.sarawan.android.model.data

import com.squareup.moshi.Json
import ru.sarawan.android.framework.ui.base.mainCatalog.CardType

data class CategoryDataModel(
    @field:Json(name = "id") val id: Long,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "categories") val categories: List<Category>
)

data class Category(
    @field:Json(name = "id") val id: Long,
    @field:Json(name = "name") val name: String
)

fun CategoryDataModel.toMainScreenDataModel() = CardScreenDataModel(
    id = id,
    itemDescription = name,
    cardType = CardType.STRING.type
)

fun Category.toFilter() = Filter(
    id = id,
    name = name
)