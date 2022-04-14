package ru.sarawan.android.model.data.address.yandexMap

import com.squareup.moshi.Json

enum class KindType(val value: String) {
    @field:Json(name = "country")
    COUNTRY("country"),

    @field:Json(name = "province")
    PROVINCE("province"),

    @field:Json(name = "area")
    AREA("area"),

    @field:Json(name = "locality")
    LOCALITY("locality"),

    @field:Json(name = "district")
    DISTRICT("district"),

    @field:Json(name = "street")
    STREET("street"),

    @field:Json(name = "house")
    HOUSE("house")
}