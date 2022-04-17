package ru.sarawan.android.model.data.address.yandexMap

import com.squareup.moshi.Json

data class ResponseMap(

    @field: Json(name = "GeoObjectCollection")
    val geoObjectCollection: GeoObjectCollection? = null
)