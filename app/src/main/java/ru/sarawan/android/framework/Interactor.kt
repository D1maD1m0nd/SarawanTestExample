package ru.sarawan.android.framework

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.Query

interface Interactor<T> {

    fun getData(query: Query, fromRemoteSource: Boolean): Single<T>
}