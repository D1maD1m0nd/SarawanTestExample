package ru.sarawan.android.model.datasource

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.Products
import ru.sarawan.android.model.data.Response
import ru.sarawan.android.model.datasource.api.ApiService
import ru.sarawan.android.utils.constants.SortBy
import javax.inject.Inject

class ProductsRetrofitImpl @Inject constructor(
    private val apiService: ApiService
) : ProductsDataSource {

    override fun getProducts(products: Products): Single<Response> {
        val queryMap: MutableMap<String, String?> = mutableMapOf(
            "page" to products.page.toString(),
            "page_size" to products.pageSize.toString()
        )
        products.categoryFilter?.let { queryMap["sarawan_category"] = it.toString() }
        products.discountProduct?.let { queryMap["discount_products"] = it.toString() }
        products.id?.let { queryMap["id"] = it.toString() }
        products.popularProducts?.let { queryMap["popular_products"] = it.toString() }
        products.productName?.let { queryMap["product_name"] = it }
        products.similarProducts?.let { queryMap["similar_product"] = it.toString() }
        products.subcategory?.let { queryMap["category"] = it.toString() }
        products.sortBy?.let { sorting ->
            when (sorting) {
                SortBy.PRICE_ASC -> queryMap["ordering_price"] = "true"
                SortBy.PRICE_DES -> queryMap["ordering_price"] = "false"
                SortBy.ALPHABET -> queryMap["order"] = "name"
                SortBy.DISCOUNT -> queryMap["order"] = "-discount"
            }
        }

        return apiService.getProducts(queryMap)
    }
}