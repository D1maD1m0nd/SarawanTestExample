package ru.sarawan.android.utils

import ru.sarawan.android.model.data.AddressItem

fun formatAddress(address: AddressItem): String {
    val city = address.city
    val street = address.street
    val house = address.house
    val roomNum = address.roomNumber
    return "$city, ул $street, д $house, кв $roomNum"
}