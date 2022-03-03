package ru.sarawan.android.model.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.CategoryDataModel
import ru.sarawan.android.model.data.Product
import ru.sarawan.android.model.data.Products
import ru.sarawan.android.model.data.Response

interface ProductInteractor {
    fun getProducts(products: Products): Single<Response>
    fun getProduct(id: Long): Single<Product>
    fun getCategories(): Single<List<CategoryDataModel>>
}