package com.example.sarawan.model.datasource

import com.example.sarawan.model.data.Basket
import com.example.sarawan.model.data.DataModel
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("http:/")
    fun search(@Query("") wordToSearch: String): Observable<List<DataModel>>

    /**
     * Получение категорий
     */
    @GET("api/categories/")
    fun getCategories()

    /**
     * Получение списка продуктов
     * @param page номер страницы
     */
    @GET("api/products/")
    fun getProducts(@Query("page") page : Int)

    /**
     * Получение продукта по Id
     * @param id - id продукта
     */
    @GET("api/products/{id}/")
    fun getProduct(@Path("id") id : Long)

    /**
     * Получение корзины пользователя
     */
    @GET("api/basket/")
    fun getBasket() : Call<Basket>

    /**
     * Добавление продукта в корзину
     */
    @POST("api/basket/")
    fun addProductBasket()

    /**
     * Обновление продукта
     */
    @PUT("/api/basket/{id}/")
    fun updateBasketProduct(@Path("id") id : Long)
}
