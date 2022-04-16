package ru.sarawan.android.model.data.address.yandexMap

import com.squareup.moshi.Json

enum class KindType {
    @field:Json(name = "country")
    COUNTRY,

    @field:Json(name = "province")
    PROVINCE,

    @field:Json(name = "area")
    AREA,

    @field:Json(name = "locality")
    LOCALITY,

    @field:Json(name = "district")
    DISTRICT,

    @field:Json(name = "street")
    STREET,

    @field:Json(name = "house")
    HOUSE
}