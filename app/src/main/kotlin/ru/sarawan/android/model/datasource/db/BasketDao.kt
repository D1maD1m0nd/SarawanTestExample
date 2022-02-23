package ru.sarawan.android.model.datasource.db

import androidx.room.*

@Dao
interface BasketDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(product: RoomDataModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg products: RoomDataModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(products: List<RoomDataModel>)

    @Update
    fun update(product: RoomDataModel)

    @Update
    fun update(vararg products: RoomDataModel)

    @Update
    fun update(products: List<RoomDataModel>)

    @Query("DELETE FROM ${RoomDataModel.TABLE_NAME} WHERE id = :id")
    fun deleteById(id: Long)

    @Query("DELETE FROM ${RoomDataModel.TABLE_NAME}")
    fun deleteAll()

    @Delete
    fun delete(product: RoomDataModel)

    @Delete
    fun delete(vararg products: RoomDataModel)

    @Delete
    fun delete(products: List<RoomDataModel>)

    @Query("SELECT * FROM ${RoomDataModel.TABLE_NAME}")
    fun getAll(): List<RoomDataModel>

    @Query("SELECT * FROM ${RoomDataModel.TABLE_NAME} WHERE name = :name")
    fun getByProductName(name: String): List<RoomDataModel>?
}