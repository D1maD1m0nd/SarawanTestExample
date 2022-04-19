package ru.sarawan.android.model.data

import com.squareup.moshi.Json
import ru.sarawan.android.model.data.order.OrderInformation
import ru.sarawan.android.model.data.order.OrderStatus

data class OrderApprove(
    @field:Json(name = "id")
    val id: Int,

    @field:Json(name = "dostavista_order_id")
    val orderId: Int,

    @field:Json(name = "tracking_url")
    val trackingUrl: String,

    @field:Json(name = "created_datetime")
    val createdDatetime: String,

    @field:Json(name = "order_name")
    val orderName: String,

    @field:Json(name = "status")
    val status: OrderStatus,

    @field:Json(name = "order_status")
    val orderStatus: OrderStatus,

    @field:Json(name = "information")
    val information: OrderInformation
)