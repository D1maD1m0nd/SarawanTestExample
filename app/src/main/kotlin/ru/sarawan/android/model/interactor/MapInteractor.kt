package ru.sarawan.android.model.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.address.sarawan.AddressItem

interface MapInteractor {
    fun getAddressMetaData(lat: Double, lon: Double): Single<AddressItem>
}