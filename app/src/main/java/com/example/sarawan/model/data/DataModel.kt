package com.example.sarawan.model.data

import com.google.gson.annotations.SerializedName

class DataModel(
    @field:SerializedName("id") val id: Int?,
    @field:SerializedName("price") val price: Float?,
    @field:SerializedName("name") val itemDescription: String?,
    @field:SerializedName("picture") val pictureUrl: String?,
    @field:SerializedName("discount") val discount: Int?,
    @field:SerializedName("shop") val shop: String?,
    @field:SerializedName("weight") val weight: String?,
)
