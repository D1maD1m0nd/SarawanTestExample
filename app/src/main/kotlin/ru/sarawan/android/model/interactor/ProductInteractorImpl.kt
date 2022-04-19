package ru.sarawan.android.model.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.CategoryDataModel
import ru.sarawan.android.model.data.product.Product
import ru.sarawan.android.model.data.product.Products
import ru.sarawan.android.model.data.product.Response
import ru.sarawan.android.model.datasource.CategoriesDataSource
import ru.sarawan.android.model.datasource.ProductDataSource
import ru.sarawan.android.model.datasource.ProductsDataSource
import javax.inject.Inject

class ProductInteractorImpl @Inject constructor(
    private val productRepository: ProductDataSource,
    private val productsRepository: ProductsDataSource,
    private val categoriesRepository: CategoriesDataSource,
) : ProductInteractor {
    override fun getProducts(products: Products): Single<Response> =
        productsRepository.getProducts(products)

    override fun getProduct(id: Long): Single<Product> = productRepository.getProduct(id)

    override fun getCategories(): Single<List<CategoryDataModel>> =
        categoriesRepository.getCategories()
}