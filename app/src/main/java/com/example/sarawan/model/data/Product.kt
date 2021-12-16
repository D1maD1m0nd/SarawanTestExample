package com.example.sarawan.model.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

data class Response(
    @field: Json(name = "results") val results: List<Product>,
    @field: Json(name = "count") val count: Int
)

data class Product(
    @field: Json(name = "id") val id: Long? = null,
    @field: Json(name = "images") val images: List<Image>? = null,
    @field: Json(name = "name") val name: String? = null,
    @field: Json(name = "price_type") val priceType: String? = null,
    @field: Json(name = "store_prices") val storePrices: List<StorePrice>? = null,
    @field: Json(name = "unit_quantity") val unitQuantity: String? = null,
    @field: Json(name = "description") val description: String? = null,
    var count: Int = 0
)

@Parcelize
data class StorePrice(
    @field: Json(name = "discount") val discount: Int,
    @field: Json(name = "id") val id: Int,
    @field: Json(name = "price") val price: String,
    @field: Json(name = "store") val store: String,
    var count: Int
) : Parcelable

@Parcelize
data class Image(
    @field: Json(name = "image") val image: String
) : Parcelable

data class ProductsUpdate(
    @field: Json(name = "products") val products: List<ProductShortItem>
)

data class ProductShortItem(
    @field: Json(name = "product") val product: Int,
    @field: Json(name = "quantity") val quantity: Int
)

