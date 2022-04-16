package ru.sarawan.android.model.datasource

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.address.yandexMap.MapYandex

interface MapDataSource {
    fun getAddressMetaData(coordinates: String): Single<MapYandex>
}