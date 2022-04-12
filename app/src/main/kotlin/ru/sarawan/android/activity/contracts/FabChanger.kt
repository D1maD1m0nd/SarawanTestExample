package ru.sarawan.android.activity.contracts

interface FabChanger {
    fun putPrice(price: Float)
    fun changePrice(price: Float)
    fun changeState()
}