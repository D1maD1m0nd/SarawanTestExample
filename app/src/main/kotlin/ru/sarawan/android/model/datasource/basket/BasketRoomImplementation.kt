package ru.sarawan.android.model.datasource.basket

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.basket.Basket
import ru.sarawan.android.model.data.product.Products
import ru.sarawan.android.model.data.product.ProductsUpdate
import ru.sarawan.android.model.data.product.Response
import ru.sarawan.android.model.datasource.product.ProductsDataSource
import ru.sarawan.android.model.datasource.db.SarawanDatabase
import ru.sarawan.android.model.datasource.db.toProduct
import ru.sarawan.android.model.datasource.db.toProductsItem
import ru.sarawan.android.model.datasource.db.toRoomDataModel
import javax.inject.Inject

class BasketRoomImplementation @Inject constructor(
    private val db: SarawanDatabase
) : BasketLocalDataSource, ProductsDataSource {

    override fun getBasket(): Single<Basket> = Single.fromCallable {
        val productsItems = db.basketDao
            .getAll()
            .map { it.toProductsItem() }
        Basket(-1, productsItems)
    }

    override fun clearBasket(): Single<Basket> = Single.fromCallable {
        db.basketDao.deleteAll()
        Basket(id = -1)
    }

    override fun deleteProduct(id: Int): Single<Basket> = Single.fromCallable {
        db.basketDao.deleteById(id.toLong())
        Basket(id = -1)
    }

    override fun putProduct(productItem: ProductsUpdate): Single<Basket> = Single.fromCallable {
        db.basketDao.insert(productItem.products.first().toRoomDataModel())
        Basket(id = -1)
    }

    override fun updateProduct(id: Int, productItem: ProductsUpdate): Single<Basket> =
        Single.fromCallable {
            if (productItem.products.first().quantity == 0)
                db.basketDao.deleteById(productItem.products.first().id ?: -1)
            else db.basketDao.insert(productItem.products.first().toRoomDataModel())
            Basket(id = -1)
        }

    override fun getProducts(products: Products): Single<Response> = Single.fromCallable {
        val product = if (products.productName != null) db.basketDao
            .getByProductName(name = products.productName)
            ?.map { it.toProduct() } ?: emptyList() else emptyList()
        Response(product, null, null)
    }
}
