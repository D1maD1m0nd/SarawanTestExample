package ru.sarawan.android.model.datasource.product

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.product.Products
import ru.sarawan.android.model.data.product.Response

interface ProductsDataSource {
    fun getProducts(products: Products): Single<Response>
}