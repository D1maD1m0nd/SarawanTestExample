package com.example.sarawan.model.data

import com.google.gson.annotations.SerializedName

class BasketDataModel(
    @SerializedName("id") val id: Int?,
    @SerializedName("price_per_unit") val price: Float?,
    @SerializedName("count") val count: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("picture") val pictureUrl: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("company") val company: String?,
    @SerializedName("shop") val shop: String?,
    @SerializedName("weight") val weight: String?,
)

