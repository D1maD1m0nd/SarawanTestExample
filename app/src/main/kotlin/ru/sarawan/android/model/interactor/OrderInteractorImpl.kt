package ru.sarawan.android.model.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.*
import ru.sarawan.android.model.datasource.OrderDataSource
import javax.inject.Inject

class OrderInteractorImpl @Inject constructor(
    private val remoteRepository: OrderDataSource
) : OrderInteractor {
    override fun getPreCalculationOrder(address: AddressItem): Single<Order> =
        remoteRepository.getPreCalculationOrder(address)

    override fun createOrder(address: AddressItem): Single<OrderApprove> =
        remoteRepository.createOrder(address)

    override fun getOrders(): Single<List<OrderApprove>> = remoteRepository.getOrders()

    override fun cancelOrder(id: Int): Single<OrderApprove> = remoteRepository.cancelOrder(id)

    override fun deleteOrder(): Single<UserDataModel> = remoteRepository.deleteOrder()

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