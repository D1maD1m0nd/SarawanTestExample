package ru.sarawan.android.model.data.address.yandexMap

import com.squareup.moshi.Json

data class MetaDataProperty(


    @field: Json(name = "GeocoderMetaData")
    val geocoderMetaData: GeocoderMetaData? = null
)