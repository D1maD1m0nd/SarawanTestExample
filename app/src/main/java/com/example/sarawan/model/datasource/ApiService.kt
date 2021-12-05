package com.example.sarawan.model.datasource

import com.example.sarawan.model.data.*
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*
import retrofit2.http.Query

interface ApiService {

    /**
     * Получение продукта по имени
     */
    @GET("products/")
    fun search(
        @Query("product_name") productName: String,
        @Query("page") page: Int
    ): Single<Response>

    /**
     * Получение продуктов с скидкой
     */
    @GET("products/")
    fun getDiscountProducts(
        @Query("discount_products") discountProducts: Boolean = true,
        @Query("page") page: Int
    ): Single<Response>

    /**
     * Получение популярных продуктов
     */
    @GET("products/")
    fun getPopularProducts(
        @Query("popular_products") popularProducts: Boolean = true,
        @Query("page") page: Int
    ): Single<Response>

    /**
     * Получение похожих продуктов
     */
    @GET("products/")
    fun getSimilarProducts(@Query("similar_product") storeId: Long) : Single<Response>
    /**
     * Получение категорий
     */
    @GET("categories/")
    fun getCategories(): Single<List<CategoryDataModel>>

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
    fun putBasketProduct(@Body productItem: ProductsUpdate): Single<Basket>

    /**
     * Обновление продукта
     */
    @PUT("basket/{id}/")
    fun updateBasketProduct(@Path("id") id: Int, @Body productItem: ProductsUpdate): Single<Basket>

    /**
     * Удаление продукта из корзины
     */
    @DELETE("basket_product/{id}/")
    fun deleteBasketProduct(@Path("id") id: Int): Single<Basket>
}
