package com.example.sarawan.model.data

import com.google.gson.annotations.SerializedName

class DataModel(
    @field:SerializedName("id") val id: Int?,
    @field:SerializedName("price") val price: Float?,
    @field:SerializedName("name") val word: String?
)
