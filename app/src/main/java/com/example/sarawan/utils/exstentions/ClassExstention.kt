package com.example.sarawan.utils.exstentions

import com.example.sarawan.model.data.Product
import com.example.sarawan.model.data.ProductShortItem
import com.example.sarawan.model.data.ProductsItem

fun Product.toProductShortItem() : ProductShortItem {
    var id = 0
    this.storePrices?.firstOrNull()?.id?.let {
        id = it
    }
    val count: Int = this.count

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

fun Double.toFormatString(postfix : String)  = String.format("%.2f $postfix", this)
