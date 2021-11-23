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

fun convertFromProduct(product: Product): MainScreenDataModel {
    return MainScreenDataModel(
        price = product.store_prices?.get(0)?.price?.toFloat(),
        itemDescription = product.name,
        pictureUrl = product.images?.get(0)?.image,
        discount = product.store_prices?.get(0)?.discount,
        shop = product.store_prices?.get(0)?.store,
        cardType = when (product.store_prices?.get(0)?.discount) {
            is Int -> {
                if (product.store_prices[0].discount > 0) CardType.TOP.type
                else CardType.COMMON.type
            }
            else -> CardType.EMPTY.type
        }
    )

}
