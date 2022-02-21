package ru.sarawan.android.model.datasource

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.Query

interface DataSource<T : Any> {
    fun getData(query: Query): Single<T>
}
