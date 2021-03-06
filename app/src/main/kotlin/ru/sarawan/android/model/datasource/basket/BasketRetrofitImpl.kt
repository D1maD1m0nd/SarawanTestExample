package ru.sarawan.android.model.datasource.basket

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.basket.Basket
import ru.sarawan.android.model.data.product.ProductsResponse
import ru.sarawan.android.model.data.product.ProductsUpdate
import ru.sarawan.android.model.datasource.api.ApiService
import javax.inject.Inject

class BasketRetrofitImpl @Inject constructor(
    private val apiService: ApiService
) : BasketDataSource {
    override fun getBasket(): Single<Basket> = apiService.getBasket()

    override fun clearBasket(): Single<Basket> = apiService.clearBasket()

    override fun deleteProduct(id: Int): Single<Basket> =
        apiService.deleteBasketProduct(id)

    override fun putProduct(productItem: ProductsUpdate): Single<ProductsResponse> =
        apiService.putBasketProduct(productItem)

    override fun updateProduct(id: Int, productItem: ProductsUpdate): Single<ProductsResponse> =
        apiService.updateBasketProduct(id, productItem)
}