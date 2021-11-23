package com.example.sarawan.model.data

import com.example.sarawan.model.data.DelegatesModel.BasketListItem
import com.squareup.moshi.Json
import java.util.*


data class ProductsItem(

    @field:Json(name="product")
    val product: Product? = null,

    @field:Json(name="quantity")
    val quantity: Int? = null,

    @field:Json(name="basket_product_id")
    val basketProductId: Int? = null
) : BasketListItem {
    override val id: Long
        get() = Random().nextLong()
}

