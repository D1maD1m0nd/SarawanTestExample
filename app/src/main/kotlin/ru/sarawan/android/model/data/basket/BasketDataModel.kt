package ru.sarawan.android.model.data

import com.squareup.moshi.Json
import ru.sarawan.android.model.data.basket.ProductsItem

data class Basket(
    @field:Json(name = "basket_id") val basketId: Int? = null,
    @field:Json(name = "products") val products: List<ProductsItem>? = null,
    @field:Json(name = "id") val id: Int? = null
)
