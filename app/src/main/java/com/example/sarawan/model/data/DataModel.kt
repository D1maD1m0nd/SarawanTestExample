package com.example.sarawan.model.data

import com.google.gson.annotations.SerializedName

data class DataModel(
    @SerializedName("id") val id: Int?,
    @SerializedName("price") val price: Float?,
    @SerializedName("name") val itemDescription: String?,
    @SerializedName("picture") val pictureUrl: String?,
    @SerializedName("discount") val discount: Int?,
    @SerializedName("shop") val shop: String?,
    @SerializedName("weight") val weight: String?,
)
