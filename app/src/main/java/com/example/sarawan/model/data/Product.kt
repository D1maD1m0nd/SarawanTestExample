package com.example.sarawan.model.data

import com.squareup.moshi.Json

data class Response(
    @field: Json(name = "results") val results: List<Product>
)

data class Product(
    @field: Json(name = "id") val id: Long? = null,
    @field: Json(name = "images") val images: List<Image>? = null,
    @field: Json(name = "name") val name: String? = null,
    @field: Json(name = "price_type") val price_type: String? = null,
    @field: Json(name = "store_prices") val store_prices: List<StorePrice>? = null,
    @field: Json(name = "unit_quantity") val unit_quantity: String? = null,
)

data class StorePrice(
    @field: Json(name = "discount") val discount: Int,
    @field: Json(name = "id") val id: Int,
    @field: Json(name = "price") val price: String,
    @field: Json(name = "store") val store: String
)

data class Image(
    @field: Json(name = "image") val image: String
)