package com.example.sarawan.model.datasource.repository

import com.example.sarawan.model.data.Basket
import com.example.sarawan.model.datasource.ApiService
import retrofit2.Callback

class RepositoryImpl(private val api : ApiService) : Repository {
    override fun getBasket(callback: Callback<Basket>) {
        api.getBasket().enqueue(callback)
    }
}