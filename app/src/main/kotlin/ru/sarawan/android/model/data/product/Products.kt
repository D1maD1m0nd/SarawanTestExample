package ru.sarawan.android.model.data.product

import ru.sarawan.android.utils.constants.SortBy

data class Products(
    val id: Long? = null,
    val productName: String? = null,
    var page: Int = 1,
    val pageSize: Int = 20,
    val discountProduct: Boolean? = null,
    val popularProducts: Boolean? = null,
    val similarProducts: Long? = null,
    val categoryFilter: Int? = null,
    val subcategory: Int? = null,
    val sortBy: SortBy? = null,
)