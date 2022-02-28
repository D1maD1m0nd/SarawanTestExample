package ru.sarawan.android.framework

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.AddressItem
import ru.sarawan.android.model.data.Query
import ru.sarawan.android.model.datasource.DataSource
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
