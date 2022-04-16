package ru.sarawan.android.model.data.address.yandexMap

import com.squareup.moshi.Json

data class GeoObject(

    @field: Json(name = "metaDataProperty")
    val metaDataProperty: MetaDataProperty? = null,

    @field: Json(name = "name")
    val name: String? = null,

    @field: Json(name = "description")
    val description: String? = null
)