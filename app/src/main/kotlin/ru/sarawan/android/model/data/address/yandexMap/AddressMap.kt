package ru.sarawan.android.model.data.address.yandexMap

import com.squareup.moshi.Json
import ru.sarawan.android.model.data.AddressItem

data class AddressMap(

    @field: Json(name = "Components")
    val components: List<ComponentsItem>? = null,
)

