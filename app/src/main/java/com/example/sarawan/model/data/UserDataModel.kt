package com.example.sarawan.model.data

import com.squareup.moshi.Json

data class UserDataModel(

	@field:Json(name="birthday")
	val birthday: String? = null,

	@field:Json(name="phone")
	val phone: String? = null,

	@field:Json(name="last_name")
	val lastName: String? = null,

	@field:Json(name="id")
	val id: Int? = null,

	@field:Json(name="first_name")
	val firstName: String? = null,

	@field:Json(name="email")
	val email: String? = null,

	@field:Json(name="status")
	val status: String? = null
)

data class UserRegistration(
    @field:Json(name = "phone_number")
    val phoneNumber: String? = null,
    @field:Json(name = "success")
    val success: Boolean? = null,
    @field:Json(name = "error")
    val error : String? = null,
    @field:Json(name = "token")
    val token : String? = null,
    @field:Json(name = "user_id")
    val user_id : Long? = null,
	@field:Json(name = "code")
	val code : Int? = null
)