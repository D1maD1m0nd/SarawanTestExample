package com.example.sarawan.model.datasource

import com.example.sarawan.model.data.Query
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class RetrofitImplementation @Inject constructor(private val apiService: ApiService) :
    DataSource<List<*>> {

    override fun getData(query: Query): Single<List<*>> {
        return when (query) {

            is Query.Get -> when (query) {

                is Query.Get.Products -> when (query) {
                    Query.Get.Products.DiscountProducts -> apiService
                        .getDiscountProducts()
                        .map { it.results }

                    is Query.Get.Products.Id -> apiService
                        .getProduct(query.id)
                        .map { listOf(it) }

                    is Query.Get.Products.Page -> apiService
                        .getProducts(query.page)
                        .map { it.results }

                    Query.Get.Products.PopularProducts -> apiService
                        .getPopularProducts()
                        .map { it.results }

                    is Query.Get.Products.ProductName -> apiService
                        .search(query.productName)
                        .map { it.results }
                    is Query.Get.Products.SimilarProducts -> apiService
                        .getSimilarProducts(query.id)
                        .map { it.results }
                }

                Query.Get.Basket -> apiService
                    .getBasket()
                    .map { listOf(it) }
            }

            is Query.Post -> when (query) {

                is Query.Post.Basket.Put -> apiService
                    .putBasketProduct(query.products)
                    .map { listOf(it) }
            }

            is Query.Put -> when (query) {

                is Query.Put.Basket.Update -> apiService
                    .updateBasketProduct(query.id, query.products)
                    .map { listOf(it) }
            }

            is Query.Delete -> when (query) {

                is Query.Delete.Basket.Remove -> apiService
                    .deleteBasketProduct(query.id)
                    .map { listOf(it) }
            }
        }
    }

    override fun saveData(dataSet: List<*>) = Unit
}
