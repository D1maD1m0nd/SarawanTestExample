package com.example.sarawan.model.data

import androidx.annotation.ColorInt
import com.example.sarawan.framework.ui.base.mainCatalog.CardType

data class MainScreenDataModel(

    val id: Long? = null,
    val storeId: Int? = null,
    val itemDescription: String? = null,
    var quantity: Int? = null,
    val unitQuantity: String? = null,
    val discount: Int? = null,
    val shop: String? = null,
    val priceType: String? = null,
    val weight: String? = null,
    val pictureUrl: String? = null,
    val price: Float? = null,
    var cardType: Int? = null,
    val fontSize: Float? = null,
    val gravity: Int? = null,
    val fontType: Int? = null,
    val extData: List<Any>? = null,
    @ColorInt val backgroundColor: Int? = null,
    @ColorInt val textColor: Int? = null,
    val padding: ArrayList<Int>? = null,
    val sortText: String? = null,
)

fun Product.toMainScreenDataModel(sortText: String): MainScreenDataModel {
    return MainScreenDataModel(
        id = id,
        storeId = storePrices?.first()?.id,
        price = storePrices?.first()?.price?.toFloat(),
        itemDescription = name,
        priceType = priceType,
        pictureUrl = images?.first()?.image,
        discount = storePrices?.first()?.discount,
        unitQuantity = unitQuantity,
        shop = storePrices?.first()?.store,
        cardType = CardType.COMMON.type,
        quantity = storePrices?.first()?.count,
        sortText = sortText
    )
}

fun MainScreenDataModel.toProduct() =
    Product(
        id = id,
        images = listOf(Image(pictureUrl ?: "")),
        product = storeId,
        name = itemDescription,
        priceType = priceType,
        storePrices = mutableListOf(
            StorePrice(
                discount = discount ?: 0,
                id = storeId ?: 0,
                price = price?.toDouble() ?: 0.0,
                store = shop ?: "",
                count = quantity ?: 0
            )
        ),
        unitQuantity = unitQuantity,
        quantity = quantity ?: 0
    )
