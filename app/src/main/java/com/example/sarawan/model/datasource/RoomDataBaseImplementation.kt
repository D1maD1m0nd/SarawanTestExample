package com.example.sarawan.model.datasource

import com.example.sarawan.R
import com.example.sarawan.model.data.DataModel
import com.example.sarawan.model.datasource.db.SarawanDatabase
import com.example.sarawan.utils.convertDataModelToRoomModel
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class RoomDataBaseImplementation @Inject constructor(
    private val db: SarawanDatabase
) : DataSource<List<DataModel>> {

    override fun getData(word: String): Observable<List<DataModel>> {
        return Observable.fromCallable {

            getTestData()

//            db.searchDao.getByWord(word.lowercase())?.map {
//                convertRoomModelToDataModel(it)
//            }
        }
    }

    override fun saveData(dataSet: List<DataModel>) {
        dataSet.forEach {
            db.searchDao.insert(convertDataModelToRoomModel(it))
        }
    }

    private fun getTestData(): List<DataModel> {
        val result: MutableList<DataModel> = mutableListOf()
        for (i in 0..50) {
            result.add(
                DataModel(
                    i,
                    getRandomPrice(),
                    "Some very long description",
                    R.drawable.test_card_img.toString(),
                    getRandomDiscount(),
                    "АШАН",
                    "100г",
                    "Россия",
                    "Сарафан"
                )
            )
        }
        return result
    }

    private fun getRandomPrice(): Float = (Math.random() * 10_000).toFloat()

    private fun getRandomDiscount(): Int = (Math.random() * 100).toInt()


}
