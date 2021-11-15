package com.example.sarawan.model.datasource

import com.example.sarawan.model.data.DataModel
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("http:/")
    fun search(@Query("") wordToSearch: String): Observable<List<DataModel>>
}
