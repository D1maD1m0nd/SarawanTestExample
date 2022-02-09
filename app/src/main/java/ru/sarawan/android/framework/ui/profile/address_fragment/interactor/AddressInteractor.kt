package ru.sarawan.android.framework.ui.profile.address_fragment.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.AddressItem

class AddressInteractor : IAddressInteractor {
    override fun validateAddress(addressItem: AddressItem) : Single<String> {
        val city  = addressItem.city
        val street = addressItem.street
        val house = addressItem.house
        val roomNumber = addressItem.roomNumber
        val message = when {
            city.isEmpty() -> {
                "Город не заполнен"
            }
            street.isEmpty() -> {
                "Улица не заполнен"
            }
            house.isEmpty() -> {
                "Дом не заполнен"
            }
            roomNumber.isEmpty() -> {
                "Номер не заполнен"
            }
            else -> {""}
        }
        return Single.just(message)
    }
}