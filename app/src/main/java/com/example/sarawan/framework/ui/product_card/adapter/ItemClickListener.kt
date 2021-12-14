package com.example.sarawan.framework.ui.product_card.adapter

import com.example.sarawan.model.data.Product
import com.example.sarawan.model.data.TypeCardEnum

interface ItemClickListener {
    fun openProductCard(productId : Int)
    fun update(pos : Int = 0, mode : Boolean = false, type : TypeCardEnum)
    fun create(product: Product, pos: Int, type: TypeCardEnum)
}