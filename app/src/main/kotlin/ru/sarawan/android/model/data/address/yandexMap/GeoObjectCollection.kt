package ru.sarawan.android.model.data.address.yandexMap

import com.squareup.moshi.Json

data class GeoObjectCollection(
    @field: Json(name = "featureMember")
    val featureMember: List<FeatureMemberItem>? = null
)