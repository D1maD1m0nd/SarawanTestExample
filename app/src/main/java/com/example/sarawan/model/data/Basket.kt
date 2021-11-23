package com.example.sarawan.model.data

import com.squareup.moshi.Json

data class Basket(

	@field:Json(name="basket_id")
	val basketId: Int? = null,

	@field:Json(name="products")
	val products: List<ProductsItem?>? = null
)
