package ru.sarawan.android.framework.ui.basket.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.Order
import ru.sarawan.android.model.data.ProductsItem

class BasketInteractor : IBasketInteractor {
    override fun calculateOrder(data: List<ProductsItem>): Single<Order> {
        val count = data.sumOf { it.quantity ?: 0 }
        val weight = data.sumOf {
            it.basketProduct
                ?.basketProduct
                ?.unitQuantity
                ?.toDouble()
                ?.times(it.quantity!!) ?: 0.0
        }
        val price = data.sumOf {
            it.basketProduct
                ?.price
                ?.toDouble()
                ?.times(it.quantity!!) ?: 0.0
        }

        return Single.just(
            Order(
                basketCount = count,
                paymentAmount = 0.0,
                deliveryAmount = 0.0,
                basketSumm = price,
                weight = weight
            )
        )
    }
}