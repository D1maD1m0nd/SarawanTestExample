package ru.sarawan.android.model.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.Basket
import ru.sarawan.android.model.data.ProductsUpdate
import ru.sarawan.android.model.datasource.BasketDataSource
import javax.inject.Inject

class BasketInteractorImpl @Inject constructor(
    private val remoteRepository: BasketDataSource,
    private val localRepository: BasketDataSource
) : BasketInteractor {
    override fun getBasket(isFromRemote: Boolean): Single<Basket> =
        if (isFromRemote) remoteRepository.getBasket() else localRepository.getBasket()

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