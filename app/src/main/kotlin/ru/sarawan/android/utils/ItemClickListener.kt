package ru.sarawan.android.utils

import ru.sarawan.android.model.data.ProductsItem
import ru.sarawan.android.model.data.delegatesModel.BasketListItem

interface ItemClickListener {
    fun update(pos: Int = 0, mode: Boolean = false)
    fun deleteItem(productsItem: ProductsItem, pos: Int, item: BasketListItem)
    fun openProductCard(productId: Int)
    fun openOrderCard()
    fun clear()
}