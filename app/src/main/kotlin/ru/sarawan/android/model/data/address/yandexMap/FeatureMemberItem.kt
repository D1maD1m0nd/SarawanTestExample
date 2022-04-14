package ru.sarawan.android.model.data.address.yandexMap

import com.squareup.moshi.Json

data class FeatureMemberItem(

    @field: Json(name = "GeoObject")
    val geoObject: GeoObject? = null
)