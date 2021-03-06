package ru.sarawan.android.model.datasource.basket

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.basket.Basket
import ru.sarawan.android.model.data.product.ProductsUpdate

interface BasketLocalDataSource {
    fun getBasket(): Single<Basket>
    fun clearBasket(): Single<Basket>
    fun deleteProduct(id: Int): Single<Basket>
    fun putProduct(productItem: ProductsUpdate): Single<Basket>
    fun updateProduct(id: Int, productItem: ProductsUpdate): Single<Basket>
}