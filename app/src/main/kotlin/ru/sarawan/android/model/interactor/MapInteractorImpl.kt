package ru.sarawan.android.model.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.address.sarawan.AddressItem
import ru.sarawan.android.model.data.address.yandexMap.toAddress
import ru.sarawan.android.model.datasource.map.MapDataSource
import javax.inject.Inject

class MapInteractorImpl @Inject constructor(
    private val remoteRepository: MapDataSource,
) : MapInteractor {
    override fun getAddressMetaData(lat: Double, lon: Double): Single<AddressItem> {
        val coordinates = "$lon,$lat"
        return remoteRepository.getAddressMetaData(coordinates).map {
            it.toAddress(lat = lat, lon = lon)
        }
    }
}