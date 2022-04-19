package ru.sarawan.android.model.datasource.api

import io.reactivex.rxjava3.core.Single
import retrofit2.http.*
import ru.sarawan.android.model.data.*
import ru.sarawan.android.model.data.address.sarawan.AddressItem
import ru.sarawan.android.model.data.product.Product
import ru.sarawan.android.model.data.product.ProductsResponse
import ru.sarawan.android.model.data.product.ProductsUpdate
import ru.sarawan.android.model.data.product.Response

interface ApiService {

    /**
     * Получение продуктов
     */
    @GET("api/products/")
    fun getProducts(
        @QueryMap map: Map<String, String?>
    ): Single<Response>

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
    fun clearBasket(): Single<Basket>

    /**
     * Добавление продукта в корзину
     */
    @POST("api/basket/")
    fun putBasketProduct(@Body productItem: ProductsUpdate): Single<Basket>

    /**
     * Обновление продукта
     */
    @PUT("api/basket/{id}/")
    fun updateBasketProduct(
        @Path("id") id: Int,
        @Body productItem: ProductsUpdate
    ): Single<Basket>

    /**
     * Удаление продукта из корзины
     */
    @DELETE("api/basket_product/{id}/")
    fun deleteBasketProduct(@Path("id") id: Int): Single<Basket>

    /**
     * Получение пользователя по id
     */
    @GET("api/user/{id}/")
    fun getUser(@Path("id") id: Long): Single<UserDataModel>

    /**
     * Обновление пользователя
     */
    @PUT("api/user/{id}/")
    fun updateUser(@Path("id") id: Long, @Body user: UserDataModel): Single<UserDataModel>

    /**
     * Создание пользователя
     */
    @POST("users/authentication/api/get-token/")
    fun createUser(@Body user: UserRegistration): Single<UserRegistration>

    /**
     * Отправка смс
     */
    @POST("users/authentication/api/sms-send/")
    fun sendSms(@Body user: UserRegistration): Single<UserRegistration>

    /**
     * Создание нового адреса
     */
    @POST("api/user_address/")
    fun createAddress(@Body address: AddressItem): Single<AddressItem>

    /**
     * Получение списка адресов
     */
    @GET("api/user_address/")
    fun getAddress(): Single<List<AddressItem>>

    /**
     * Расчет стоимости заказа
     */
    @POST("api/order_calculate/")
    fun getPreCalculationOrder(@Body address: AddressItem): Single<Order>

    /**
     * Оформление заказа
     */
    @POST("api/order_approve/")
    fun createOrder(@Body address: AddressItem): Single<OrderApprove>

    /**
     * Получение списка заказов
     */
    @GET("api/order/")
    fun getOrders(): Single<List<OrderApprove>>

    /**
     * Удаление заказа
     */
    @POST("api/order/{id}/cancel/ ")
    fun cancelOrder(@Path("id") id: Int): Single<OrderApprove>
}
