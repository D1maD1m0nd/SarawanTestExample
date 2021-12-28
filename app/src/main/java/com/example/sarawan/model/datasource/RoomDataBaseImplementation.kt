package com.example.sarawan.model.datasource

import com.example.sarawan.model.data.Basket
import com.example.sarawan.model.data.BasketResponse
import com.example.sarawan.model.data.Query
import com.example.sarawan.model.data.Response
import com.example.sarawan.model.datasource.db.SarawanDatabase
import com.example.sarawan.model.datasource.db.toProduct
import com.example.sarawan.model.datasource.db.toProductsItem
import com.example.sarawan.model.datasource.db.toRoomDataModel
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class RoomDataBaseImplementation @Inject constructor(
    private val db: SarawanDatabase
) : DataSource<List<*>> {

    override fun getData(query: Query): Single<List<*>> {
        return Single.fromCallable {
            when (query) {
                is Query.Get -> when (query) {
                    is Query.Get.Products -> when (query) {
                        is Query.Get.Products.ProductName -> db.basketDao
                            .getByProductName(name = query.productName)
                            ?.map { it.toProduct() }
                            ?.also { return@fromCallable listOf(Response(it, it.size)) }
                        else -> throwError()
                    }
                    Query.Get.Basket -> db.basketDao
                        .getAll()
                        .map { it.toProductsItem() }
                        .also { return@fromCallable listOf(Basket(-1, it)) }
                    else -> throwError()
                }
                is Query.Delete -> when (query) {
                    is Query.Delete.Basket -> when (query) {
                        is Query.Delete.Basket.Remove -> {
                            db.basketDao.deleteById(query.id.toLong())
                            listOf(BasketResponse(-1))
                        }
                        Query.Delete.Basket.Clear ->  {
                            db.basketDao.deleteAll()
                            listOf(BasketResponse(-1))
                        }
                    }
                    else -> throwError()
                }
                is Query.Post -> when (query) {
                    is Query.Post.Basket.Put -> {
                        db.basketDao.insert(query.products.products.first().toRoomDataModel())
                        listOf(BasketResponse(-1))
                    }
                    else -> throwError()
                }
                is Query.Put -> when (query) {
                    is Query.Put.Basket.Update -> {
                        if (query.products.products.first().quantity == 0) db.basketDao.deleteById(query.products.products.first().id ?: -1)
                        else db.basketDao.insert(query.products.products.first().toRoomDataModel())
                        listOf(BasketResponse(-1))
                    }
                    else -> throwError()
                }
            }
        }
    }

    private fun throwError(): Nothing = throw RuntimeException("No such method in DataBase")
}
