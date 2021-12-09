package com.example.sarawan.model.data.delegatesModel

import java.util.*

class BasketFooter(
    override var id: Long = Random().nextLong(),
    var weight: Double = 0.0,
    var price: Double = 0.0,
    var deliveryPrice: Double = 0.0,
    var resultPrice: Double = 0.0,
    var address: String = "Адрес"
) : BasketListItem
