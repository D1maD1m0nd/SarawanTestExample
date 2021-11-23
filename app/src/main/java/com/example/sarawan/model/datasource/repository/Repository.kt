package com.example.sarawan.model.datasource.repository

import com.example.sarawan.model.data.Basket
import retrofit2.Callback

interface Repository {
    fun getBasket(callback: Callback<Basket>)
}