package com.example.sarawan.model.data

import com.squareup.moshi.Json

data class OrderInformation(
	@field:Json(name="order_name")
	val orderName: String? = null,

	@field:Json(name="status")
	val status: String? = null
)
