package com.example.sarawan.model.data

import com.squareup.moshi.Json

enum class OrderStatus {
    @field:Json(name="NEW")
    NEW,
    @field:Json(name="AVL")
    AVL,
    @field:Json(name="ACT")
    ACT,
    @field:Json(name="CMD")
    CMD,
    @field:Json(name="CAN")
    CAN,
    @field:Json(name="DEL")
    DEL,
    @field:Json(name="PLD")
    PLD,
    @field:Json(name="FAL")
    FAL
}