package ru.sarawan.android.model.datasource

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.Products
import ru.sarawan.android.model.data.Response

interface ProductsDataSource {
    fun getProducts(products: Products): Single<Response>
}