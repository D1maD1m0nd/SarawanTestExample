package com.example.sarawan.model.datasource.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [RoomDataModel::class],
    version = 1
)
abstract class SarawanDatabase : RoomDatabase() {

    abstract val basketDao: BasketDao
}