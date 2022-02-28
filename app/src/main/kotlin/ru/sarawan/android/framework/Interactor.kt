package ru.sarawan.android.framework

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.AddressItem
import ru.sarawan.android.model.data.Query

interface Interactor<T : Any> {

    fun getData(query: Query, fromRemoteSource: Boolean): Single<T>
}