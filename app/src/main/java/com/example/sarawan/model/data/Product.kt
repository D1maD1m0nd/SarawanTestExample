package com.example.sarawan.model.data

import com.squareup.moshi.Json

data class Product(

    @field:Json(name="product")
    val product: Product? = null,

    @field:Json(name="price")
    val price: String? = null,

    @field:Json(name="discount")
    val discount: Int? = null,

    @field:Json(name="store")
    val store: String? = null,

    @field:Json(name="product_store_id")
    val productStoreId: Int? = null,

    @field:Json(name="images")
    val images: List<ImagesItem?>? = null,

    @field:Json(name="is_favorite")
    val isFavorite: Any? = null,

    @field:Json(name="name")
    val name: String? = null,

    @field:Json(name="price_type")
    val priceType: String? = null,

    @field:Json(name="id")
    val id: Int? = null,

    @field:Json(name="unit_quantity")
    val unitQuantity: String? = null
)