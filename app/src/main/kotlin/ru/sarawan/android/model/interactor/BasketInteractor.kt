package ru.sarawan.android.model.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.Basket
import ru.sarawan.android.model.data.product.ProductsUpdate

interface BasketInteractor {
    fun getBasket(isFromRemote: Boolean): Single<Basket>
    fun clearBasket(isFromRemote: Boolean): Single<Basket>
    fun deleteProduct(isFromRemote: Boolean, id: Int): Single<Basket>
    fun putProduct(isFromRemote: Boolean, productItem: ProductsUpdate): Single<Basket>
    fun updateProduct(isFromRemote: Boolean, id: Int, productItem: ProductsUpdate): Single<Basket>
}