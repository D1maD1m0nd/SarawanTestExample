package ru.sarawan.android.model.datasource

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.Basket
import ru.sarawan.android.model.data.MapYandex
import ru.sarawan.android.model.datasource.api.ApiService
import ru.sarawan.android.model.datasource.api.MapApiService
import javax.inject.Inject

class MapRetrofitImpl @Inject constructor(
    private val apiService: MapApiService
) : MapDataSource {
    override fun getAddressMetaData(coordinates: String): Single<MapYandex> {
        return apiService.getAddressByCoordinate(coordinates)
    }
}