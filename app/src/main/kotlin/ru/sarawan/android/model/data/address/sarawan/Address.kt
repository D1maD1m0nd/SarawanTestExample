package ru.sarawan.android.model.data.address.sarawan

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(

    @field:Json(name = "Address")
    val address: List<AddressItem>? = null
) : Parcelable