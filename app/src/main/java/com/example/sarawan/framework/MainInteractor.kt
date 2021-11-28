package com.example.sarawan.framework

import com.example.sarawan.model.data.Query
import com.example.sarawan.model.datasource.DataSource
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class MainInteractor @Inject constructor(
    private val remoteRepository: DataSource<List<*>>,
    private val localRepository: DataSource<List<*>>
) : Interactor<List<*>> {

    override fun getData(
        query: Query,
        fromRemoteSource: Boolean
    ): Single<List<*>> {
        return if (fromRemoteSource) {
            remoteRepository.getData(query)
        } else {
            localRepository.getData(query)
        }
    }
}
