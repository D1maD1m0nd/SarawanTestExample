package ru.sarawan.android.framework.ui.profile.address_fragment.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.AddressItem

interface IAddressInteractor {
    fun validateAddress(addressItem: AddressItem) : Single<String>
}