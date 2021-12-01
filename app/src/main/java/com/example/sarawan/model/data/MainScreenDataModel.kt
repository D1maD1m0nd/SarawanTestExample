package com.example.sarawan.model.data

import androidx.annotation.ColorInt
import com.example.sarawan.framework.ui.main.adapter.CardType

data class MainScreenDataModel(

    val id: Long? = null,
    val itemDescription: String? = null,
    var quantity: Int? = null,
    val discount: Int? = null,
    val shop: String? = null,
    val weight: String? = null,
    val pictureUrl: String? = null,
    val price: Float? = null,
    val cardType: Int? = null,
    val fontSize: Float? = null,
    val gravity: Int? = null,
    val fontType: Int? = null,
    @ColorInt val backgroundColor: Int? = null,
    val padding: ArrayList<Int>? = null,
)

fun Product.convertToMainScreenDataModel(): MainScreenDataModel {
    return MainScreenDataModel(
        id = id,
        price = store_prices?.first()?.price?.toFloat(),
        itemDescription = name,
        pictureUrl = images?.first()?.image,
        discount = store_prices?.first()?.discount,
        shop = store_prices?.first()?.store,
        cardType = when (store_prices?.first()?.discount) {
            is Int -> {
                if (store_prices.first().discount > 0) CardType.TOP.type
                else CardType.COMMON.type
            }
            else -> CardType.EMPTY.type
        }
    )
}

fun MainScreenDataModel.convertToProductShortItem(): ProductShortItem {
    return ProductShortItem(
        product = id?.toInt() ?: 0,
        quantity = quantity ?: 0
    )
}
