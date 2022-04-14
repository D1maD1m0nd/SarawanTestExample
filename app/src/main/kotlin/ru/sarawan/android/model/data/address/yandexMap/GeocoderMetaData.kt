package ru.sarawan.android.model.data.address.yandexMap

import com.squareup.moshi.Json

data class GeocoderMetaData(

    @field: Json(name = "Address")
    val address: AddressMap? = null,
)