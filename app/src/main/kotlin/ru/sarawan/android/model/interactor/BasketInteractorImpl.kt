package ru.sarawan.android.model.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.basket.Basket
import ru.sarawan.android.model.data.basket.ProductDto
import ru.sarawan.android.model.data.basket.ProductInformation
import ru.sarawan.android.model.data.product.ProductsResponse
import ru.sarawan.android.model.data.product.ProductsUpdate
import ru.sarawan.android.model.data.product.toProductDto
import ru.sarawan.android.model.data.basket.toProductDto
import ru.sarawan.android.model.datasource.basket.BasketDataSource
import ru.sarawan.android.model.datasource.basket.BasketLocalDataSource
import ru.sarawan.android.utils.MoshiAdapters.MoshiCustomAdapter
import javax.inject.Inject

class BasketInteractorImpl @Inject constructor(
    private val remoteRepository: BasketDataSource,
    private val localRepository: BasketLocalDataSource,
    private val moshiCustomAdapter: MoshiCustomAdapter,
) : BasketInteractor {
    override fun getBasket(isFromRemote: Boolean): Single<Basket> {
        val item = if (isFromRemote) remoteRepository.getBasket() else localRepository.getBasket()
        return item.map { basket ->
            basket.products?.map { item ->
                var info: ProductInformation? = null
                val json =
                    item.basketProduct?.basketProduct?.information?.productInformation
                json?.let { newJson ->
                    info = moshiCustomAdapter.infoFromJson(newJson)
                }
                item.basketProduct?.basketProduct?.information = info
                item
            }
            basket
        }
    }


    override fun clearBasket(isFromRemote: Boolean): Single<Basket> =
        if (isFromRemote) remoteRepository.clearBasket() else localRepository.clearBasket()

    override fun deleteProduct(isFromRemote: Boolean, id: Int): Single<Basket> =
        if (isFromRemote) remoteRepository.deleteProduct(id) else localRepository.deleteProduct(id)

    override fun putProduct(
        isFromRemote: Boolean,
        productItem: ProductsUpdate
    ): Single<ProductDto> {
        val item =
            if (isFromRemote) remoteRepository.putProduct(productItem) else localRepository.putProduct(
                productItem
            )
        return item.map {
            when (it) {
                is Basket -> {
                    it.toProductDto()
                }

                is ProductsResponse -> {
                    it.toProductDto()
                }
                else -> {
                    ProductDto()
                }
            }
        }
    }


    override fun updateProduct(
        isFromRemote: Boolean,
        id: Int,
        productItem: ProductsUpdate
    ): Single<ProductDto> {
        val item = if (isFromRemote) remoteRepository.updateProduct(
            id,
            productItem
        ) else localRepository.updateProduct(id, productItem)
        return item.map {
            when (it) {
                is Basket -> {
                    it.toProductDto()
                }

                is ProductsResponse -> {
                    it.toProductDto()
                }
                else -> {
                    ProductDto()
                }
            }
        }
    }

}