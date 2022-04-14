package ru.sarawan.android.model.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.AddressItem
import ru.sarawan.android.model.data.address.yandexMap.KindType
import ru.sarawan.android.model.data.address.yandexMap.MapYandex
import ru.sarawan.android.model.data.address.yandexMap.toAddress
import ru.sarawan.android.model.datasource.MapDataSource
import javax.inject.Inject

class MapInteractorImpl @Inject constructor(
    private val remoteRepository: MapDataSource,
) : MapInteractor {
    override fun getAddressMetaData(coordinates: String): Single<AddressItem> {
        return remoteRepository.getAddressMetaData(coordinates).map {
            val address = it.response
                ?.geoObjectCollection
                ?.featureMember
                ?.firstOrNull()
                ?.geoObject
                ?.metaDataProperty
                ?.geocoderMetaData
                ?.address
                ?.toAddress()
            address
        }
    }
}