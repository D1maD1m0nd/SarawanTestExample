package com.example.sarawan.model.datasource

import com.example.sarawan.model.data.Query
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class DataSourceRemote @Inject constructor(private val remoteProvider: RetrofitImplementation) :
    DataSource<List<*>> {

    override fun getData(query: Query): Single<List<*>> = remoteProvider.getData(query)

    override fun saveData(dataSet: List<*>) = remoteProvider.saveData(dataSet)
}
