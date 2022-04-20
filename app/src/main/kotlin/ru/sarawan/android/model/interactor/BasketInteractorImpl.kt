package ru.sarawan.android.model.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.di.annotations.Local
import ru.sarawan.android.model.data.Basket
import ru.sarawan.android.model.data.basket.ProductInformation
import ru.sarawan.android.model.data.product.ProductsUpdate
import ru.sarawan.android.model.datasource.basket.BasketDataSource
import ru.sarawan.android.utils.MoshiAdapters.MoshiCustomAdapter
import javax.inject.Inject

class BasketInteractorImpl @Inject constructor(
    private val remoteRepository: BasketDataSource,
    @Local private val localRepository: BasketDataSource,
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

    override fun putProduct(isFromRemote: Boolean, productItem: ProductsUpdate): Single<Basket> =
        if (isFromRemote) remoteRepository.putProduct(productItem)
        else localRepository.putProduct(productItem)

    override fun updateProduct(
        isFromRemote: Boolean,
        id: Int,
        productItem: ProductsUpdate
    ): Single<Basket> = if (isFromRemote) remoteRepository.updateProduct(id, productItem)
    else localRepository.updateProduct(id, productItem)

}