package com.example.sarawan.model.datasource.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sarawan.model.data.*
import com.example.sarawan.model.datasource.db.RoomDataModel.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class RoomDataModel(

    @PrimaryKey val id: Long,
    val image: String? = null,
    val name: String? = null,
    val priceType: String? = null,
    val unitQuantity: String? = null,
    val description: String? = null,
    val discount: Int? = null,
    val shopID: Int? = null,
    val price: Double? = null,
    val store: String? = null,
    var count: Int = 0
) {
    companion object {
        const val TABLE_NAME = "Basket"
    }
}

fun RoomDataModel.toProduct() =
    Product(
        id = id,
        images = listOf(Image(image ?: "")),
        product = id.toInt(),
        name = name,
        priceType = priceType,
        storePrices = mutableListOf(
            StorePrice(
                discount = discount ?: -1,
                id = shopID ?: -1,
                price = price ?: -1.0,
                store = store ?: "",
                count = count
            )
        ),
        unitQuantity = unitQuantity,
        description = description,
        quantity = count
    )

fun RoomDataModel.toProductsItem() =
    ProductsItem(
        quantity = count,
        basketProductId = -1,
        basketProduct = BasketProduct(
            productStoreId = shopID,
            store = store,
            price = price.toString(),
            discount = discount,
            basketProduct = BasketProduct(
                storePrices = listOf(
                    StorePrice(
                        discount = discount ?: -1,
                        id = shopID ?: -1,
                        price = price ?: -1.0,
                        store = store ?: "",
                        count = count
                    )
                ),
                id = id,
                name = name,
                priceType = priceType,
                unitQuantity = unitQuantity,
                images = listOf(Image(image ?: ""))
            )

        )
    )

fun Product.toRoomDataModel() =
    RoomDataModel(
        id = id ?: -1,
        image = images?.firstOrNull()?.image,
        name = name,
        priceType = priceType,
        unitQuantity = unitQuantity,
        description = description,
        discount = storePrices?.first()?.discount,
        shopID = storePrices?.first()?.id,
        price = storePrices?.first()?.price,
        store = storePrices?.first()?.store,
        count = quantity
    )