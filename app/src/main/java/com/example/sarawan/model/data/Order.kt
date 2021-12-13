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
	val basketSum: Double? = null,

	@field:Json(name="total_weight_kg")
	val totalWeightKg: String? = null,

	@field:Json(name="vehicle_type_id")
	val vehicleTypeId: String? = null,

	@field:Json(name="delivery_fee_amount")
	val deliveryFeeAmount: String? = null,

	@field:Json(name="adress_id")
	val adressId: Int? = null,

	@field:Json(name="error")
	val error: String? = null,

	@field:Json(name="order_id")
	val orderId: String? = null,

	@field:Json(name="tracking_url")
	val trackingUrl: String? = null,

	@field:Json(name="created_datetime")
	val createdDatetime: String? = null,

	@field:Json(name="order_name")
	val orderName: String? = null,

	@field:Json(name="status")
	val status: String? = null
)
