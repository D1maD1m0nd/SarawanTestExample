package com.example.sarawan.model.data

import com.squareup.moshi.Json


data class ImagesItem(

    @field:Json(name="image")
    val image: String? = null,

    @field:Json(name="id")
    val id: Int? = null
)