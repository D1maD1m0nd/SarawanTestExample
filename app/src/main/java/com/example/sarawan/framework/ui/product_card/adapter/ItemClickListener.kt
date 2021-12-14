package com.example.sarawan.framework.ui.product_card.adapter

import com.example.sarawan.model.data.Product

interface ItemClickListener {
    fun openProductCard(productId : Int)
    fun update(pos : Int = 0, mode : Boolean = false)
    fun create(product : Product, pos : Int)
}