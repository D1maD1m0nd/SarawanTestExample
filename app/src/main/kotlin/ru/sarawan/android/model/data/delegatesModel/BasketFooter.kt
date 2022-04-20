package ru.sarawan.android.model.data.delegatesModel

import ru.sarawan.android.model.data.address.sarawan.AddressItem
import java.util.*

class BasketFooter(
    override var id: Long = Random().nextLong(),
    var weight: Double = 0.0,
    var price: Double = 0.0
) : BasketListItem
