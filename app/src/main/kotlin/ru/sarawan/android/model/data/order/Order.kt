package ru.sarawan.android.model.data

import com.squareup.moshi.Json

data class Order(


    @field:Json(name = "basket_count")
    val basketCount: Int,

    @field:Json(name = "payment_amount")
    val paymentAmount: Double,

    @field:Json(name = "delivery_amount")
    val deliveryAmount: Double,

    @field:Json(name = "basket_summ")
    val basketSumm: Double,

    val weight: Double
)

