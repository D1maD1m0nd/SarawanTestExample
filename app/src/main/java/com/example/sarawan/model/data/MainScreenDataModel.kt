package com.example.sarawan.model.data

import androidx.annotation.ColorInt
import com.example.sarawan.framework.ui.base.mainCatalog.CardType

data class MainScreenDataModel(

    val id: Long? = null,
    val itemDescription: String? = null,
    var quantity: Int? = null,
    val discount: Int? = null,
    val shop: String? = null,
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
)

fun Product.toMainScreenDataModel(): MainScreenDataModel {
    return MainScreenDataModel(
        id = id,
        price = storePrices?.first()?.price?.toFloat(),
        itemDescription = name,
        pictureUrl = images?.first()?.image,
        discount = storePrices?.first()?.discount,
        shop = storePrices?.first()?.store,
        cardType = when (storePrices?.first()?.discount) {
            is Int -> {
                if (storePrices.first().discount > 0) CardType.TOP.type
                else CardType.COMMON.type
            }
            else -> CardType.EMPTY.type
        }
    )
}

fun MainScreenDataModel.toProductShortItem(): ProductShortItem {
    return ProductShortItem(
        product = id?.toInt() ?: 0,
        quantity = quantity ?: 0
    )
}
