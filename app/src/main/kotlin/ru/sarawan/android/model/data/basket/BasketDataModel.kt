package ru.sarawan.android.model.data.basket

import com.squareup.moshi.Json

data class Basket(
    @field:Json(name = "basket_id") val basketId: Int? = null,
    @field:Json(name = "products") val products: List<ProductsItem>? = null,
    @field:Json(name = "id") val id: Int? = null
)


fun Basket.toProductDto(): ProductDto {
    return ProductDto(id = this.basketId)
}