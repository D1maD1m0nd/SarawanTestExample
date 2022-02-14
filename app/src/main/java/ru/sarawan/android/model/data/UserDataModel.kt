package ru.sarawan.android.model.data

import com.squareup.moshi.Json

data class UserDataModel(

    @field:Json(name = "phone")
    val phone: String = "",

    @field:Json(name = "last_name")
    val lastName: String = "",

    @field:Json(name = "id")
    val id: Long = 0,

    @field:Json(name = "first_name")
    val firstName: String = "",

    @field:Json(name = "basket")
    val basket: Basket? = null,
)

data class UserRegistration(
    @field:Json(name = "phone_number")
    val phoneNumber: String = "",
    @field:Json(name = "success")
    val success: Boolean = false,
    @field:Json(name = "error")
    val error: String = "",
    @field:Json(name = "token")
    val token: String = "",
    @field:Json(name = "user_id")
    val userId: Long = 0,
    @field:Json(name = "code")
    val code: String = ""
)