package com.example.sarawan.model.datasource

import com.example.sarawan.model.data.Query
import com.example.sarawan.utils.SortBy
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class RetrofitImplementation @Inject constructor(private val apiService: ApiService) :
    DataSource<List<*>> {

    override fun getData(query: Query): Single<List<*>> {
        return when (query) {

            is Query.Get -> when (query) {
                is Query.Get.Users -> when (query) {
                    is Query.Get.Users.UserData -> apiService
                        .getUser(query.id)
                        .map { mutableListOf(it) }
                }
                is Query.Get.Products -> when (query) {
                    is Query.Get.Products.DiscountProducts -> apiService
                        .getDiscountProducts(
                            page = query.page,
                            order = when (query.sortBy) {
                                SortBy.PRICE_ASC -> "price"
                                SortBy.PRICE_DES -> "-price"
                                SortBy.ALPHABET -> "name"
                                SortBy.DISCOUNT -> "-discount"
                            }
                        )
                        .map { mutableListOf(it) }

                    is Query.Get.Products.Id -> apiService
                        .getProduct(query.id)
                        .map { mutableListOf(it) }

                    is Query.Get.Products.PopularProducts -> apiService
                        .getPopularProducts(page = query.page)
                        .map { mutableListOf(it) }

                    is Query.Get.Products.ProductName -> apiService
                        .search(query.productName, query.page)
                        .map { mutableListOf(it) }
                    is Query.Get.Products.SimilarProducts -> apiService
                        .getSimilarProducts(query.id, query.page)
                        .map { it.results }
                    is Query.Get.Products.ProductCategory -> apiService
                        .getCategoryProducts(
                            query.productCategory, query.page,
                            order = when (query.sortBy) {
                                SortBy.PRICE_ASC -> "price"
                                SortBy.PRICE_DES -> "-price"
                                SortBy.ALPHABET -> "name"
                                SortBy.DISCOUNT -> "-discount"
                            }
                        )
                        .map { mutableListOf(it) }
                }
                is Query.Get.Orders -> {
                    when (query) {
                        is Query.Get.Orders.Order -> apiService
                            .getPreCalculationOrder(query.address)
                            .map { listOf(it) }
                    }
                }
                Query.Get.Basket -> apiService
                    .getBasket()
                    .map { listOf(it) }

                Query.Get.Category -> apiService
                    .getCategories()
                    .map { it as List<*> }

                Query.Get.Address -> apiService
                    .getAddress()
                    .map { it }
            }

            is Query.Post -> when (query) {
                is Query.Post.Basket.Put -> apiService
                    .putBasketProduct(query.products)
                    .map { listOf(it) }
                is Query.Post.User.Sms -> apiService
                    .sendSms(query.user)
                    .map { listOf(it) }
                is Query.Post.User.NewUser -> apiService
                    .createUser(query.user)
                    .map { listOf(it) }
                is Query.Post.Address.NewAddress -> apiService
                    .createAddress(query.address)
                    .map { listOf(it) }
                is Query.Post.Order.Create -> apiService
                    .createOrder(query.address)
                    .map { listOf(it) }
            }

            is Query.Put -> when (query) {

                is Query.Put.Basket.Update -> apiService
                    .updateBasketProduct(query.id, query.products)
                    .map { listOf(it) }
                is Query.Put.Users.Update -> apiService
                    .updateUser(query.id, query.user)
                    .map { listOf(it) }
            }

            is Query.Delete -> when (query) {

                is Query.Delete.Basket.Remove -> apiService
                    .deleteBasketProduct(query.id)
                    .map { listOf(it) }
                is Query.Delete.Basket.Clear -> apiService
                    .clearBasket()
                    .map { listOf(it) }
            }
        }
    }

    override fun saveData(dataSet: List<*>) = Unit
}
