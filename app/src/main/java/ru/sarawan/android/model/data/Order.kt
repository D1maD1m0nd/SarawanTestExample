package ru.sarawan.android.model.data

import com.squareup.moshi.Json

data class Order(


	@field:Json(name="basket_count")
	val basketCount: Int? = null,

	@field:Json(name="payment_amount")
	val paymentAmount: Double? = null,

	@field:Json(name="delivery_amount")
	val deliveryAmount: Double? = null,

	@field:Json(name="basket_summ")
	val basketSumm: Double? = null,

	val weight : Double? = null
)

data class OrderApprove(

	@field:Json(name="dostavista_order_id")
	val orderId: Int? = null,

	@field:Json(name="tracking_url")
	val trackingUrl: String? = null,

	@field:Json(name="created_datetime")
	val createdDatetime: String? = null,

	@field:Json(name="order_name")
	val orderName: String? = null,

	@field:Json(name="status")
	val status: OrderStatus? = null,

	@field:Json(name="order_status")
	val orderStatus: OrderStatus? = null,

	@field:Json(name="information")
	val information : OrderInformation? = null
)
