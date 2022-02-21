package ru.sarawan.android.framework.ui.profile.address_fragment.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.AddressItem
import ru.sarawan.android.utils.constants.AddressState

class AddressInteractor : IAddressInteractor {
    override fun validateAddress(addressItem: AddressItem): Single<AddressState> {
        val city = addressItem.city
        val street = addressItem.street
        val house = addressItem.house
        val roomNumber = addressItem.roomNumber
        val state = when {
            city.isEmpty() -> {
                AddressState.CITY
            }
            street.isEmpty() -> {
                AddressState.STREET
            }
            house.isEmpty() -> {
                AddressState.HOUSE
            }
            roomNumber.isEmpty() -> {
                AddressState.NUMBER
            }
            else -> {
                AddressState.VALID
            }
        }
        return Single.just(state)
    }
}