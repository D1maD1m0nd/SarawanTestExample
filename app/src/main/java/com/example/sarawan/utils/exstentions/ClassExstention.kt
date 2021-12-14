package com.example.sarawan.utils

import com.example.sarawan.model.data.ProductShortItem
import com.example.sarawan.model.data.ProductsItem

fun ProductsItem.toUpdateProduct() : ProductShortItem{
    var id = 0
    var count = 0
    this.basketProduct?.productStoreId?.let {
        id = it
    }

    this.quantity?.let {
        count = it
    }
    return ProductShortItem(id, count)
}