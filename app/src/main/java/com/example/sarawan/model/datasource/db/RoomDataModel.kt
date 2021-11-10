package com.example.sarawan.model.datasource.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomDataModel(

    @PrimaryKey val id: Long,
    val price: Float?,
    val word: String?
)