package ru.sarawan.android.model.data.basket


data class ProductDto(val id: Int? = -1, var items: List<ProductItemDto>? = null)

data class ProductItemDto(
    var quantity: Int? = null,
)