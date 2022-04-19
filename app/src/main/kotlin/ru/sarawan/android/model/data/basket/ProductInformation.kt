package ru.sarawan.android.model.data.basket

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductInformation(
    @field:Json(name = "product_information") val productInformation: String,
    @field:Json(name = "brand") val brand: String? = null,
    @field:Json(name = "manufacturing_country") val country: String? = null
) : Parcelable