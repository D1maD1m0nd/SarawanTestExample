package ru.sarawan.android.model.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.Order
import ru.sarawan.android.model.data.OrderApprove
import ru.sarawan.android.model.data.UserDataModel
import ru.sarawan.android.model.data.address.sarawan.AddressItem
import ru.sarawan.android.model.data.basket.ProductsItem

interface OrderInteractor {
    fun getPreCalculationOrder(address: AddressItem): Single<Order>
    fun createOrder(address: AddressItem): Single<OrderApprove>
    fun getOrders(): Single<List<OrderApprove>>
    fun cancelOrder(id: Int): Single<OrderApprove>
    fun deleteOrder(): Single<UserDataModel>
    fun calculateOrder(data: List<ProductsItem>): Single<Order>
}