package com.example.sarawan.model.data

import com.example.sarawan.model.data.DelegatesModel.BasketListItem
import com.squareup.moshi.Json

data class DataModel(
    @field:Json(name="id") override val id: Long,
    @field:Json(name ="price") val price: Float? = null,
    @field:Json(name = "name") val itemDescription: String? = null,
    @field:Json(name ="picture") val pictureUrl: String? = null,
    @field:Json(name ="discount") val discount: Int? = null,
    @field:Json(name ="shop") val shop: String? = null,
    @field:Json(name ="weight") val weight: Int? = null,
    @field:Json(name= "country") val country: String? = null,
    @field:Json(name = "company") val company: String? = null,
    @field:Json(name ="quantity") var quantity: Long? = null,
    @field:Json(name = "cardType") val cardType: Int? = null,
) : BasketListItem
