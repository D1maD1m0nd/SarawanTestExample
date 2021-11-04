package com.example.sarawan.utils

import com.example.sarawan.model.data.DataModel
import com.example.sarawan.model.datasource.db.RoomDataModel

fun convertRoomModelToDataModel(roomModel: RoomDataModel) =
    DataModel(roomModel.id, roomModel.price, roomModel.word, null, null, null, null, null, null)

fun convertDataModelToRoomModel(dataModel: DataModel) =
    RoomDataModel(dataModel.id ?: 0, dataModel.price, dataModel.itemDescription)