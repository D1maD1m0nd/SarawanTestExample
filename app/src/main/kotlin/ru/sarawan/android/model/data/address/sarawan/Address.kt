package ru.sarawan.android.model.data.address.sarawan

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize
import ru.sarawan.android.model.data.AddressItem

@Parcelize
data class Address(

    @field:Json(name = "Address")
    val address: List<AddressItem>? = null
) : Parcelable