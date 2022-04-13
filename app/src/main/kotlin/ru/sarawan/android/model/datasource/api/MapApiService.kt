package ru.sarawan.android.model.datasource.api

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ru.sarawan.android.model.data.MapYandex
import ru.sarawan.android.model.data.UserDataModel

interface MapApiService {
    @GET
    fun getAddressByCoordinate(@Query("geocode") geocode: String): Single<MapYandex>
}