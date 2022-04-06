package ru.sarawan.android.model.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(

    @field:Json(name = "Address")
    val address: List<AddressItem>? = null
) : Parcelable

@Parcelize
data class AddressItem(

    @field:Json(name = "city")
    val city: String = "",

    @field:Json(name = "housing")
    val housing: String = "",

    @field:Json(name = "street")
    val street: String = "",

    @field:Json(name = "room_number")
    val roomNumber: String = "",

    @field:Json(name = "id")
    val id: Int = 0,

    @field:Json(name = "adress_id")
    val idAddressOrder: Int = 0,

    @field:Json(name = "house")
    val house: String = "",

    @field:Json(name = "user")
    val user: Long = 0,

    @field:Json(name = "building")
    val building: String = "",

    @field:Json(name = "primary")
    val primary: Boolean = false,

    var addressFull: String = ""
) : Parcelable