package ru.sarawan.android.model.data.address.yandexMap

import com.squareup.moshi.Json
import ru.sarawan.android.model.data.AddressItem

data class AddressMap(

    @field: Json(name = "Components")
    val components: List<ComponentsItem>? = null,
)

fun AddressMap.toAddress(): AddressItem {
    val self = this
    return AddressItem().apply {
        self.components?.map { item ->
            when (item.kind) {

                KindType.LOCALITY.value -> {
                    city = item.name
                }

                KindType.HOUSE.value -> {
                    house = item.name
                }

                KindType.STREET.value -> {
                    street = item.name
                }

            }

        }
    }
}