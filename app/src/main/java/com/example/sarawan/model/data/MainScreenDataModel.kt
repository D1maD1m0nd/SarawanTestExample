package com.example.sarawan.model.data

import androidx.annotation.ColorInt

data class MainScreenDataModel(
    val price: Float? = null,
    val itemDescription: String? = null,
    val pictureUrl: String? = null,
    val discount: Int? = null,
    val shop: String? = null,
    val weight: Int? = null,
    var quantity: Long? = null,
    val cardType: Int? = null,
    val fontSize: Float? = null,
    val gravity: Int? = null,
    val fontType: Int? = null,
    @ColorInt val backgroundColor: Int? = null,
    val padding: ArrayList<Int>? = null,
)

fun convertFromDataModel(dataModel: DataModel): MainScreenDataModel {
    return MainScreenDataModel(
        price = dataModel.price,
        itemDescription = dataModel.itemDescription,
        pictureUrl = dataModel.pictureUrl,
        discount = dataModel.discount,
        shop = dataModel.shop,
        weight = dataModel.weight,
        quantity = dataModel.quantity,
        cardType = dataModel.cardType,
    )
}
