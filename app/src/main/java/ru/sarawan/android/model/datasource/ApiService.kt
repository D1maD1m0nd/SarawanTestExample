package ru.sarawan.android.model.datasource

import io.reactivex.rxjava3.core.Single
import retrofit2.http.*
import retrofit2.http.Query
import ru.sarawan.android.model.data.*

interface ApiService {

    /**
     * Получение продукта по имени
     */
    @GET("api/products/")
    fun search(
        @Query("search") productName: String,
        @Query("page") page: Int
    ): Single<Response>

    /**
     * Получение продуктов с скидкой
     */
    @GET("api/products/")
    fun getDiscountProducts(
        @Query("discount_products") discountProducts: Boolean = true,
        @Query("page") page: Int,
        @Query("order") order: String,
    ): Single<Response>

    /**
     * Получение продуктов с скидкой с сортировкой по цене
     */
    @GET("api/products/")
    fun getDiscountProductsByPrice(
        @Query("discount_products") discountProducts: Boolean = true,
        @Query("page") page: Int,
        @Query("ordering_price") ordering_price: String,
    ): Single<Response>

    /**
     * Получение продуктов по главной категории
     */
    @GET("api/products/")
    fun getCategoryProducts(
        @Query("sarawan_category") categoryProducts: Int,
        @Query("page") page: Int,
        @Query("order") order: String,
    ): Single<Response>

    /**
     * Получение продуктов по главной категории с сортировкой по цене
     */
    @GET("api/products/")
    fun getCategoryProductsByPrice(
        @Query("sarawan_category") categoryProducts: Int,
        @Query("page") page: Int,
        @Query("ordering_price") ordering_price: String,
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
    fun getSimilarProducts(
        @Query("similar_product") storeId: Long,
        @Query("page") page: Int
    ) : Single<Response>

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

    @DELETE("api/basket/")
    fun clearBasket() : Single<Basket>

    /**
     * Добавление продукта в корзину
     */
    @POST("api/basket/")
    fun putBasketProduct(@Body productItem: ProductsUpdate): Single<BasketResponse>

    /**
     * Обновление продукта
     */
    @PUT("api/basket/{id}/")
    fun updateBasketProduct(@Path("id") id: Int, @Body productItem: ProductsUpdate): Single<BasketResponse>

    /**
     * Удаление продукта из корзины
     */
    @DELETE("api/basket_product/{id}/")
    fun deleteBasketProduct(@Path("id") id: Int): Single<Basket>

    /**
     * Получение пользователя по id
     */
    @GET("api/user/{id}/")
    fun getUser(@Path("id") id : Long) : Single<UserDataModel>

    /**
     * Обновление пользователя
     */
    @PUT("api/user/{id}/")
    fun updateUser(@Path("id") id : Long, @Body user : UserDataModel) : Single<UserDataModel>

    /**
     * Создание пользователя
     */
    @POST("users/authentication/api/get-token/")
    fun createUser(@Body user : UserRegistration) : Single<UserRegistration>

    /**
     * Отправка смс
     */
    @POST("users/authentication/api/sms-send/")
    fun sendSms(@Body user : UserRegistration) : Single<UserRegistration>

    /**
     * Создание нового адреса
     */
    @POST("api/user_address/")
    fun createAddress(@Body address : AddressItem) : Single<AddressItem>

    /**
     * Получение списка адресов
     */
    @GET("api/user_address/")
    fun getAddress() : Single<MutableList<AddressItem>>

    /**
     * Расчет стоимости заказа
     */
    @POST("api/order_calculate/")
    fun getPreCalculationOrder(@Body address: AddressItem) : Single<Order>

    /**
     * Оформление заказа
     */
    @POST("api/order_approve/")
    fun createOrder(@Body address : AddressItem) : Single<OrderApprove>

    /**
     * Получение списка заказов
     */
    @GET("api/order/")
    fun getOrders() : Single<List<OrderApprove>>

    /**
     * Удаление заказа
     */
    @DELETE("api/order/{id}/")
    fun deleteOrder(@Path("id") id: Int) : Single<OrderApprove>
}
