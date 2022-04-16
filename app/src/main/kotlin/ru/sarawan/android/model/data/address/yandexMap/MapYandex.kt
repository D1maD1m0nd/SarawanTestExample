package ru.sarawan.android.model.data.address.yandexMap

import com.squareup.moshi.Json
import ru.sarawan.android.model.data.AddressItem


data class MapYandex(

	@field: Json(name = "response")
	val response: ResponseMap? = null
)

fun MapYandex.toAddress(): AddressItem {
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

	return address
}


