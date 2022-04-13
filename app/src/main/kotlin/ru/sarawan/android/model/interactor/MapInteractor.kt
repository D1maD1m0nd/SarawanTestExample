package ru.sarawan.android.model.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.MapYandex
import ru.sarawan.android.model.data.ResponseMap

interface MapInteractor {
    fun getAddressMetaData(coordinates: String): Single<MapYandex>
}