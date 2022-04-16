package ru.sarawan.android.model.data.address.yandexMap

import com.squareup.moshi.Json

data class ComponentsItem(

    @field: Json(name = "kind")
    val kind: KindType? = null,

    @field: Json(name = "name")
    val name: String = ""
)