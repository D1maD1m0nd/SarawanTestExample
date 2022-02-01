package ru.sarawan.android.model.data

import com.squareup.moshi.Json

data class OrderInformation(
	@field:Json(name="order_name")
	val orderName: String,

	@field:Json(name="status")
	val status: String
)
