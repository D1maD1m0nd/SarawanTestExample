package com.example.sarawan.model.datasource

import com.example.sarawan.model.data.DataModel
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

interface ApiService {

    @GET("http:/")
    fun search(@Query("") wordToSearch: String): Observable<List<DataModel>>

    /**
     * Получение рекомендуемых продуктов
     */
    @GET("favorite_products/")
    fun getFavoriteProducts()

    /**
     * Получение продуктов с скидкой
     */
    @GET("discount_products/")
    fun getDiscountProducts()

    /**
     * Получение категорий
     */
    @GET("categories/")
    fun getCategories()

    /**
     * Получение списка продуктов
     * @param page номер страницы
     */
    @GET("products/")
    fun getProducts(@Query("page") page : Int)

    /**
     * Получение продукта по Id
     * @param id - id продукта
     */
    @GET("products/{id}/")
    fun getProduct(@Path("id") id : Long)

    /**
     * Получение корзины пользователя
     */
    @GET("basket/")
    fun getBasket()

    /**
     * Добавление продукта в корзину
     */
    @POST("basket/")
    fun addProductBasket()

    /**
     * Обновление продукта
     */
    @PUT("/api/basket/{id}/")
    fun updateBasketProduct(@Path("id") id : Long)
}
