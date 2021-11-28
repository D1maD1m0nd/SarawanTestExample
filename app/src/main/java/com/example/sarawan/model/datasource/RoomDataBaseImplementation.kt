package com.example.sarawan.model.datasource

import com.example.sarawan.model.data.Product
import com.example.sarawan.model.data.Query
import com.example.sarawan.model.datasource.db.SarawanDatabase
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RoomDataBaseImplementation @Inject constructor(
    private val db: SarawanDatabase
) : DataSource<List<*>> {

    override fun getData(query: Query): Single<List<*>> {
        return Single.fromCallable {
            emptyList<Product>() as List<*>
        }.delay(2, TimeUnit.SECONDS)
    }

    override fun saveData(dataSet: List<*>) = Unit
}
