package ru.sarawan.android.model.datasource

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.Query
import ru.sarawan.android.model.data.UserDataModel
import ru.sarawan.android.utils.constants.SortBy
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

                is Query.Get.Products -> {
                    val queryMap: MutableMap<String, String?> = mutableMapOf(
                        "page" to query.page.toString()
                    )
                    query.categoryFilter?.let { queryMap["sarawan_category"] = it.toString() }
                    query.discountProduct?.let { queryMap["discount_products"] = it.toString() }
                    query.id?.let { queryMap["id"] = it.toString() }
                    query.popularProducts?.let { queryMap["popular_products"] = it.toString() }
                    query.productName?.let { queryMap["product_name"] = it }
                    query.similarProducts?.let { queryMap["similar_product"] = it.toString() }
                    query.subcategory?.let { queryMap["category"] = it.toString() }
                    query.sortBy?.let { sorting ->
                        when (sorting) {
                            SortBy.PRICE_ASC -> queryMap["ordering_price"] = "true"
                            SortBy.PRICE_DES -> queryMap["ordering_price"] = "false"
                            SortBy.ALPHABET -> queryMap["order"] = "name"
                            SortBy.DISCOUNT -> queryMap["order"] = "-discount"
                        }
                    }

                    apiService
                        .getProducts(queryMap)
                        .map { mutableListOf(it) }
                }

                is Query.Get.ProductByID -> apiService
                    .getProduct(query.id)
                    .map { mutableListOf(it) }

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
                    .map { it }

                Query.Get.Address -> apiService
                    .getAddress()
                    .map { it }

                Query.Get.OrdersApproves -> apiService
                    .getOrders()
                    .map { it }
            }

            is Query.Post -> when (query) {

                is Query.Post.User -> when (query) {
                    is Query.Post.User.Sms -> apiService
                        .sendSms(query.user)
                        .map { listOf(it) }

                    is Query.Post.User.NewUser -> apiService
                        .createUser(query.user)
                        .map { listOf(it) }
                }

                is Query.Post.Basket.Put -> apiService
                    .putBasketProduct(query.products)
                    .map { listOf(it) }

                is Query.Post.Address.NewAddress -> apiService
                    .createAddress(query.address)
                    .map { listOf(it) }

                is Query.Post.Order.Create -> apiService
                    .createOrder(query.address)
                    .map { listOf(it) }

                is Query.Post.Order.Cancel -> {
                    apiService.cancelOrder(query.id)
                        .map {
                            listOf(it)
                        }
                }
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

                is Query.Delete.Basket -> when (query) {
                    is Query.Delete.Basket.Remove -> apiService
                        .deleteBasketProduct(query.id)
                        .map { listOf(it) }

                    is Query.Delete.Basket.Clear -> apiService
                        .clearBasket()
                        .map { listOf(it) }
                }
                is Query.Delete.Order.Delete -> {
                    Single.just(emptyList<UserDataModel>())
                }
            }
        }
    }
}
