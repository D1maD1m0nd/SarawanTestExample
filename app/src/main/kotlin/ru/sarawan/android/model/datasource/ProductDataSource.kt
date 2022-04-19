package ru.sarawan.android.model.datasource

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.product.Product

interface ProductDataSource {
    fun getProduct(id: Long): Single<Product>
}