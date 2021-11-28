package com.example.sarawan.model.datasource

import com.example.sarawan.model.data.Basket
import com.example.sarawan.model.data.Product
import com.example.sarawan.model.data.ProductsUpdate
import com.example.sarawan.model.data.Response
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

interface ApiService {

    /**
     * Получение продукта по имени
     */
    @GET("products/")
    fun search(@Query("product_name") productName: String): Single<Response>

    /**
     * Получение продуктов с скидкой
     */
    @GET("products/")
    fun getDiscountProducts(@Query("discount_products") discountProducts: Boolean = true): Single<Response>

    /**
     * Получение популярных продуктов
     */
    @GET("products/")
    fun getPopularProducts(@Query("popular_products") popularProducts: Boolean = true): Single<Response>

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
    fun getProducts(@Query("page") page: Int): Single<Response>

    /**
     * Получение продукта по Id
     * @param id - id продукта
     */
    @GET("products/{id}/")
    fun getProduct(@Path("id") id: Long): Single<Product>

    /**
     * Получение корзины пользователя
     */
    @GET("basket/")
    fun getBasket(): Single<Basket>

    /**
     * Добавление продукта в корзину
     */
    @POST("basket/")
    fun addProductBasket()

    /**
     * Обновление продукта
     */
    @PUT("basket/{id}/")
    fun updateBasketProduct(@Path("id") id: Int, @Body productItem : ProductsUpdate) : Single<Basket>

    /**
     * Удаление продукта из корзины
     */
    @DELETE("basket_product/{id}/")
    fun deleteBasketProduct(@Path("id") id : Int) : Single<Basket>
}
