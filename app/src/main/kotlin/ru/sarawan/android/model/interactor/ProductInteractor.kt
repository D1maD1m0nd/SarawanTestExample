package ru.sarawan.android.model.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.CategoryDataModel
import ru.sarawan.android.model.data.product.Product
import ru.sarawan.android.model.data.product.Products
import ru.sarawan.android.model.data.product.Response

interface ProductInteractor {
    fun getProducts(products: Products): Single<Response>
    fun getProduct(id: Long): Single<Product>
    fun getCategories(): Single<List<CategoryDataModel>>
}