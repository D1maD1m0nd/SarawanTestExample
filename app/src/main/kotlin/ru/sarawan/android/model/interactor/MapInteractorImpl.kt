package ru.sarawan.android.model.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.MapYandex
import ru.sarawan.android.model.datasource.MapDataSource
import javax.inject.Inject

class MapInteractorImpl @Inject constructor(
    private val remoteRepository: MapDataSource,
) : MapInteractor {
    override fun getAddressMetaData(coordinates: String): Single<MapYandex> {
        return remoteRepository.getAddressMetaData(coordinates)
    }
}