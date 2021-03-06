package ru.sarawan.android.model.datasource.product

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.product.Product
import ru.sarawan.android.model.datasource.api.ApiService
import javax.inject.Inject

class ProductRetrofitImpl @Inject constructor(
    private val apiService: ApiService
) : ProductDataSource {
    override fun getProduct(id: Long): Single<Product> = apiService.getProduct(id)
}