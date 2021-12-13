package com.example.sarawan.utils

import com.example.sarawan.model.data.ProductShortItem
import com.example.sarawan.model.data.ProductsItem

fun ProductsItem.toUpdateProduct() : ProductShortItem{
    var id = 0
    var count = 0
    this.basketProduct?.basketProduct?.storePrices?.let {
        if(it.isNotEmpty()) {
            id = it.first().id
        }
    }
    this.quantity?.let {
        count = it
    }
    return ProductShortItem(id, count)
}