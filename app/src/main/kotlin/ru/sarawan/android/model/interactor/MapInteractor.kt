package ru.sarawan.android.model.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.AddressItem
import ru.sarawan.android.model.data.address.yandexMap.MapYandex

interface MapInteractor {
    fun getAddressMetaData(coordinates: String): Single<AddressItem>
}