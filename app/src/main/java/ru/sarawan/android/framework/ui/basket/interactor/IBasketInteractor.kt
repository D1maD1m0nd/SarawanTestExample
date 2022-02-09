package ru.sarawan.android.framework.ui.basket.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.Order
import ru.sarawan.android.model.data.ProductsItem

interface IBasketInteractor {
    fun calculateOrder(data: List<ProductsItem>): Single<Order>
}