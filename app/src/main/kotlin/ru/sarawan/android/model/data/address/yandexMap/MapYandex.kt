package ru.sarawan.android.model.data.address.yandexMap

import com.squareup.moshi.Json
import ru.sarawan.android.model.data.address.sarawan.AddressItem


data class MapYandex(

    @field: Json(name = "response")
    val response: ResponseMap? = null
)

fun MapYandex.toAddress(lat: Double, lon: Double): AddressItem {
    val address = AddressItem()
    val metadata =
        response?.geoObjectCollection?.featureMember?.firstOrNull()?.geoObject?.metaDataProperty?.geocoderMetaData
    address.addressFull = metadata?.text ?: ""

    metadata?.address?.components?.map { item ->
        when (item.kind) {
            KindType.LOCALITY -> {
                address.city = item.name
            }

            KindType.HOUSE -> {
                address.house = item.name
            }

            KindType.STREET -> {
                address.street = item.name
            }
            else -> {}

        }
    }

    address.lat = lat
    address.lon = lon

    return address
}


