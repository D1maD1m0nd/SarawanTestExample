package ru.sarawan.android.model.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.*
import ru.sarawan.android.model.data.address.sarawan.AddressItem

interface OrderInteractor {
    fun getPreCalculationOrder(address: AddressItem): Single<Order>
    fun createOrder(address: AddressItem): Single<OrderApprove>
    fun getOrders(): Single<List<OrderApprove>>
    fun cancelOrder(id: Int): Single<OrderApprove>
    fun deleteOrder(): Single<UserDataModel>
    fun calculateOrder(data: List<ProductsItem>): Single<Order>
}