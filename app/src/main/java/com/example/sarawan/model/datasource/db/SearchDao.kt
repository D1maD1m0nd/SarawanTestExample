package com.example.sarawan.model.datasource.db

import androidx.room.*

@Dao
interface SearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(word: RoomDataModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg words: RoomDataModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(words: List<RoomDataModel>)

    @Update
    fun update(word: RoomDataModel)

    @Update
    fun update(vararg words: RoomDataModel)

    @Update
    fun update(word: List<RoomDataModel>)

    @Delete
    fun delete(word: RoomDataModel)

    @Delete
    fun delete(vararg words: RoomDataModel)

    @Delete
    fun delete(words: List<RoomDataModel>)

    @Query("SELECT * FROM RoomDataModel")
    fun getAll(): List<RoomDataModel>

    @Query("SELECT * FROM RoomDataModel WHERE word = :word")
    fun getByWord(word: String): List<RoomDataModel>?
}