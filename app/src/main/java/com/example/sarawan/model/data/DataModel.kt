package com.example.sarawan.model.data

import com.example.sarawan.model.data.DelegatesModel.BasketListItem
import com.google.gson.annotations.SerializedName

data class DataModel(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("price") val price: Float? = null,
    @SerializedName("name") val itemDescription: String? = null,
    @SerializedName("picture") val pictureUrl: String? = null,
    @SerializedName("discount") val discount: Int? = null,
    @SerializedName("shop") val shop: String? = null,
    @SerializedName("weight") val weight: Int? = null,
    @SerializedName("country") val country: String? = null,
    @SerializedName("company") val company: String? = null,
    @SerializedName("quantity") var quantity: Long? = null,
    @SerializedName("cardType") val cardType: Int? = null,
) : BasketListItem
