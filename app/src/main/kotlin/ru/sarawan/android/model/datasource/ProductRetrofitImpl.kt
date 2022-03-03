package ru.sarawan.android.model.datasource

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.Product
import javax.inject.Inject

class ProductRetrofitImpl @Inject constructor(
    private val apiService: ApiService
) : ProductDataSource {
    override fun getProduct(id: Long): Single<Product> = apiService.getProduct(id)
}