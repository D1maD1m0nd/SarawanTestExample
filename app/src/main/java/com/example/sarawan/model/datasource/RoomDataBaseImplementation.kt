package com.example.sarawan.model.datasource

import com.example.sarawan.model.data.DataModel
import com.example.sarawan.utils.convertDataModelToRoomModel
import com.example.sarawan.utils.convertRoomModelToDataModel
import com.example.sarawan.model.datasource.db.SarawanDatabase
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class RoomDataBaseImplementation @Inject constructor(private val db: SarawanDatabase) :
    DataSource<List<DataModel>> {

    override fun getData(word: String): Observable<List<DataModel>> {
        return Observable.fromCallable {
            db.searchDao.getByWord(word.lowercase())?.map {
                convertRoomModelToDataModel(it)
            }
        }
    }

    override fun saveData(dataSet: List<DataModel>) {
        dataSet.forEach {
            db.searchDao.insert(convertDataModelToRoomModel(it))
        }
    }
}
