package com.example.sarawan.model.datasource

import com.example.sarawan.model.data.*
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*
import retrofit2.http.Query

interface ApiService {

    /**
     * Получение продукта по имени
     */
    @GET("api/products/")
    fun search(
        @Query("product_name") productName: String,
        @Query("page") page: Int
    ): Single<Response>

    /**
     * Получение продуктов с скидкой
     */
    @GET("api/products/")
    fun getDiscountProducts(
        @Query("discount_products") discountProducts: Boolean = true,
        @Query("page") page: Int
    ): Single<Response>

    /**
     * Получение популярных продуктов
     */
    @GET("api/products/")
    fun getPopularProducts(
        @Query("popular_products") popularProducts: Boolean = true,
        @Query("page") page: Int
    ): Single<Response>

    /**
     * Получение похожих продуктов
     */
    @GET("api/products/")
    fun getSimilarProducts(@Query("similar_product") storeId: Long) : Single<Response>
    /**
     * Получение категорий
     */
    @GET("api/categories/")
    fun getCategories(): Single<List<CategoryDataModel>>

    /**
     * Получение продукта по Id
     * @param id - id продукта
     */
    @GET("api/products/{id}/")
    fun getProduct(@Path("id") id: Long): Single<Product>

    /**
     * Получение корзины пользователя
     */
    @GET("api/basket/")
    fun getBasket(): Single<Basket>

    /**
     * Добавление продукта в корзину
     */
    @POST("api/basket/")
    fun putBasketProduct(@Body productItem: ProductsUpdate): Single<Basket>

    /**
     * Обновление продукта
     */
    @PUT("api/basket/{id}/")
    fun updateBasketProduct(@Path("id") id: Int, @Body productItem: ProductsUpdate): Single<Basket>

    /**
     * Удаление продукта из корзины
     */
    @DELETE("api/basket_product/{id}/")
    fun deleteBasketProduct(@Path("id") id: Int): Single<Basket>

    /**
     * Создание пользователя
     */
    @POST("users/authentication/api/get-token/")
    fun createUser(@Body user : UserRegistration) : Single<UserRegistration>

    @GET("api/user/{id}/")
    fun getUser(@Path("id") id : Long) : Single<UserDataModel>
    /**
     * Отправка смс
     */
    @POST("users/authentication/api/sms-send/")
    fun sendSms(@Body user : UserRegistration) : Single<UserRegistration>

    /**
     * Создание нового адреса
     */
    @POST("api/user_address/")
    fun createAddress(@Body address : AddressItem) : Single<Address>

    @GET("api/user_address/")
    fun getAddress() : Single<MutableList<AddressItem>>
}
