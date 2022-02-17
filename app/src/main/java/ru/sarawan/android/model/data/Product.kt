package ru.sarawan.android.model.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

data class Response(
    @field: Json(name = "results") val results: List<Product>,
    @field: Json(name = "count") val count: Int,
    @field: Json(name = "search_filters") val filters: List<Filter>?
)

data class Filter(
    @field: Json(name = "id") val id: Int,
    @field: Json(name = "name") val name: String,
    @field: Json(name = "count") val count: Int
)

@Parcelize
data class Product(
    @field: Json(name = "id") val id: Long? = null,
    @field: Json(name = "images") val images: List<Image>? = null,
    @field: Json(name = "product") var product: Int? = null,
    @field: Json(name = "name") val name: String? = null,
    @field: Json(name = "price_type") val priceType: String? = null,
    @field: Json(name = "store_prices") val storePrices: MutableList<StorePrice>? = null,
    @field: Json(name = "unit_quantity") val unitQuantity: String? = null,
    @field: Json(name = "description") val description: String? = null,
    @field: Json(name = "quantity") var quantity: Int = 0
) : Parcelable

@Parcelize
data class StorePrice(
    @field: Json(name = "discount") val discount: Int,
    @field: Json(name = "id") val id: Int,
    @field: Json(name = "price") val price: Double,
    @field: Json(name = "store") val store: String,
    var count: Int
) : Parcelable

@Parcelize
data class Image(
    @field: Json(name = "image") val image: String
) : Parcelable

data class ProductsUpdate(
    @field: Json(name = "products") val products: List<Product>
)

