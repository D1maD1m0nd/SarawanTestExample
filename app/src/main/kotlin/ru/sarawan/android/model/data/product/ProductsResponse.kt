package ru.sarawan.android.model.data.product

import com.squareup.moshi.Json

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
