package ru.sarawan.android.model.data

import com.squareup.moshi.Json
import ru.sarawan.android.framework.ui.base.mainCatalog.CardType

data class CategoryDataModel(
    @field:Json(name = "id") val id: Int? = null,
    @field:Json(name = "name") val name: String? = null
)

fun CategoryDataModel.toMainScreenDataModel(): MainScreenDataModel {
    return MainScreenDataModel(
        id = id?.toLong(),
        itemDescription = name,
        cardType = CardType.STRING.type
    )
}