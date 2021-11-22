package com.example.sarawan.model.data

import com.squareup.moshi.Json

data class ProductsItem(

    @field:Json(name="product")
    val product: Product? = null,

    @field:Json(name="quantity")
    val quantity: Int? = null,

    @field:Json(name="basket_product_id")
    val basketProductId: Int? = null
)
