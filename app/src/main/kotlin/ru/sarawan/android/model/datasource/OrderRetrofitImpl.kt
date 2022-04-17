package ru.sarawan.android.model.datasource

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.address.sarawan.AddressItem
import ru.sarawan.android.model.data.Order
import ru.sarawan.android.model.data.OrderApprove
import ru.sarawan.android.model.data.UserDataModel
import ru.sarawan.android.model.datasource.api.ApiService
import javax.inject.Inject

class OrderRetrofitImpl @Inject constructor(
    private val apiService: ApiService
) : OrderDataSource {
    override fun getPreCalculationOrder(address: AddressItem): Single<Order> =
        apiService.getPreCalculationOrder(address)

    override fun createOrder(address: AddressItem): Single<OrderApprove> =
        apiService.createOrder(address)

    override fun getOrders(): Single<List<OrderApprove>> = apiService.getOrders()

    override fun cancelOrder(id: Int): Single<OrderApprove> = apiService.cancelOrder(id)

    override fun deleteOrder(): Single<UserDataModel> = Single.just(UserDataModel())
}