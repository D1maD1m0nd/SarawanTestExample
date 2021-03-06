package ru.sarawan.android.model.data.product

import com.squareup.moshi.Json
import ru.sarawan.android.model.data.basket.ProductDto

data class ProductsResponse(

    @Json(name = "id")
    val id: Int? = null,

    @Json(name = "products")
    val products: List<ProductsItem?>? = null
)

data class ProductsItem(

    @Json(name = "product")
    val product: Int? = null,

    @Json(name = "quantity")
    val quantity: Int? = null
)


fun ProductsResponse.toProductDto(): ProductDto {
    return ProductDto(id = this.id)
}