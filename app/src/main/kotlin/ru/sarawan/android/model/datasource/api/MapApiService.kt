package ru.sarawan.android.model.datasource.api

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ru.sarawan.android.model.data.address.yandexMap.MapYandex

interface MapApiService {
    @GET(".")
    fun getAddressByCoordinate(@Query("geocode") geocode: String): Single<MapYandex>
}