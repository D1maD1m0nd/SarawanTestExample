package com.example.sarawan.framework.ui.product_card.adapter

import com.example.sarawan.model.data.Product
import com.example.sarawan.utils.TypeCardEnum

interface ItemClickListener {
    fun openProductCard(productId : Long)
    fun update(pos : Int = 0, mode : Boolean = false, type : TypeCardEnum)
    fun create(product: Product, pos: Int, type: TypeCardEnum)
}