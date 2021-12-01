package com.example.sarawan.framework

import com.example.sarawan.model.data.Query
import io.reactivex.rxjava3.core.Single

interface Interactor<T> {

    fun getData(query: Query, fromRemoteSource: Boolean): Single<T>
}