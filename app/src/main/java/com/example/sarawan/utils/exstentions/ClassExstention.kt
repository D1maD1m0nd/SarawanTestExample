package com.example.sarawan.utils

import com.example.sarawan.model.data.Product
import com.example.sarawan.model.data.ProductShortItem
import com.example.sarawan.model.data.ProductsItem
import com.example.sarawan.model.data.ProductsUpdate

fun Product.toProductShortItem() : ProductShortItem {
    var id = 0
    var count = 0
    this.storePrices?.firstOrNull()?.id?.let {
        id = it
    }
    count = this.count

    return ProductShortItem(id, count)
}

fun ProductsItem.toProductShortItem() : ProductShortItem{
    var id = 0
    var count = 0
    this.basketProduct?.productStoreId?.let {
        id = it
    }

    this.quantity?.let {
        count = it
    }
    ProductShortItem(id, count)
    return ProductShortItem(id, count)
}