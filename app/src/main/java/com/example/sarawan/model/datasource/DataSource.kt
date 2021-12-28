package com.example.sarawan.model.datasource

import com.example.sarawan.model.data.Query
import io.reactivex.rxjava3.core.Single

interface DataSource<T> {
    fun getData(query: Query): Single<T>
}
