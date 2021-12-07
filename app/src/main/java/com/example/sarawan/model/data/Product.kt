package com.example.sarawan.model.data

import com.squareup.moshi.Json

data class Response(
    @field: Json(name = "results") val results: List<Product>
)


data class Product(
    @field: Json(name = "id") val id: Long? = null,
    @field: Json(name = "images") val images: List<Image>? = null,
    @field: Json(name = "name") val name: String? = null,
    @field: Json(name = "price_type") val priceType: String? = null,
    @field: Json(name = "store_prices") val storePrices: List<StorePrice>? = null,
    @field: Json(name = "unit_quantity") val unitQuantity: String? = null,
    @field: Json(name = "description") val description: String? = null,
    var visible : Boolean = true
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


data class ProductsUpdate(

    @Json(name="products")
    val products: List<ProductShortItem>
)

data class ProductShortItem(

    @Json(name="product")
    val product: Int,

    @Json(name="quantity")
    val quantity: Int
)

