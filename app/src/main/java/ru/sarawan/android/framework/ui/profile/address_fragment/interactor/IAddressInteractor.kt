package ru.sarawan.android.framework.ui.profile.address_fragment.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.AddressItem
import ru.sarawan.android.utils.constants.AddressState

interface IAddressInteractor {
    fun validateAddress(addressItem: AddressItem): Single<AddressState>
}