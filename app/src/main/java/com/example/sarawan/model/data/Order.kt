package com.example.sarawan.model.data

import com.squareup.moshi.Json

data class Order(

	@field:Json(name="basket_count")
	val basketCount: Int? = null,

	@field:Json(name="payment_amount")
	val paymentAmount: Double? = null,

	@field:Json(name="delivery_amount")
	val deliveryAmount: Double? = null,
	
	@field:Json(name="basket_summ")
	val basketSum: Double? = null
)
