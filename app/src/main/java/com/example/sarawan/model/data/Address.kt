package com.example.sarawan.model.data

import com.squareup.moshi.Json

data class Address(

	@field:Json(name="Address")
	val address: List<AddressItem>? = null
)

data class AddressItem(

	@field:Json(name="city")
	val city: String? = null,

	@field:Json(name="housing")
	val housing: String? = null,

	@field:Json(name="street")
	val street: String? = null,

	@field:Json(name="room_number")
	val roomNumber: String? = null,

	@field:Json(name="id")
	val id: Int? = null,

	@field:Json(name="adress_id")
	val idAddressOrder : Int? = null,

	@field:Json(name="house")
	val house: String? = null,

	@field:Json(name="user")
	val user: Long? = null,

	@field:Json(name="building")
	val building: String? = null,

	@field:Json(name="primary")
	val primary: Boolean? = null
)
