package ru.sarawan.android.framework.ui.product_card.adapter

import ru.sarawan.android.model.data.product.Product
import ru.sarawan.android.utils.constants.TypeCardEnum

interface ItemClickListener {
    fun openProductCard(productId: Long)
    fun update(pos: Int = 0, mode: Boolean = false, type: TypeCardEnum)
    fun create(product: Product, pos: Int, type: TypeCardEnum)
}