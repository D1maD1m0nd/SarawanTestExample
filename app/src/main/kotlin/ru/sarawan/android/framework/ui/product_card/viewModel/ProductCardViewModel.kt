package ru.sarawan.android.framework.ui.product_card.viewModel

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.Basket
import ru.sarawan.android.model.data.product.Product
import ru.sarawan.android.model.data.product.Products
import ru.sarawan.android.model.data.product.ProductsUpdate
import ru.sarawan.android.model.interactor.BasketInteractor
import ru.sarawan.android.model.interactor.ProductInteractor
import ru.sarawan.android.rx.ISchedulerProvider
import javax.inject.Inject

class ProductCardViewModel @Inject constructor(
    private val productInteractor: ProductInteractor,
    private val basketInteractor: BasketInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>() {
    private var basketID: Int? = null
    fun clear() {
        stateLiveData.value = AppState.Empty
    }

    fun similarProducts(storeId: Long?, isLoggedUser: Boolean) {
        storeId?.let { store ->
            val basket = basketInteractor.getBasket(isLoggedUser)
                .onErrorReturnItem(Basket())
            val similar = productInteractor.getProducts(Products(similarProducts = store))
            val product = productInteractor.getProduct(store)
            compositeDisposable.add(
                Single.zip(similar, basket, product) { similarData, basketData, productData ->
                    val result: MutableList<Product> = mutableListOf()
                    result.addAll(similarData.results)
                    result.add(productData)
                    val data: List<Product>
                    basketID = basketData.basketId
                    if (basketData.products?.isNotEmpty() == true) {
                        data = result.map { similarProduct ->
                            similarProduct.storePrices?.sortBy { storeItem -> storeItem.price }
                            basketData.products.forEach { basketSingleData ->
                                val similarEq =
                                    similarProduct.id == basketSingleData.basketProduct?.basketProduct?.id
                                if (similarEq) {
                                    similarProduct.quantity = basketSingleData.quantity!!
                                    val storeIdProduct =
                                        basketSingleData.basketProduct?.productStoreId
                                    storeIdProduct?.let { idProduct ->
                                        similarProduct
                                            .storePrices
                                            ?.findLast { it.id == idProduct }
                                            ?.let { it.count = basketSingleData.quantity!! }
                                    }
                                }
                            }
                            if (similarProduct.product == null) {
                                similarProduct.product =
                                    similarProduct.storePrices?.firstOrNull()?.id
                            }
                            similarProduct
                        }

                    } else {
                        data = result.map { similarProduct ->
                            similarProduct.storePrices?.sortBy { storeItem -> storeItem.price }
                            similarProduct
                        }
                    }
                    data
                }
                    .subscribeOn(schedulerProvider.computation)
                    .observeOn(schedulerProvider.ui)
                    .doOnSubscribe { stateLiveData.value = AppState.Loading }
                    .subscribe(
                        { stateLiveData.value = AppState.Success(it) },
                        { stateLiveData.value = AppState.Error(it) }
                    )
            )
        }
    }

    fun saveData(data: List<Product>, isNewItem: Boolean, isLoggedUser: Boolean) {
        if (isNewItem) compositeDisposable.add(
            basketInteractor.putProduct(isLoggedUser, ProductsUpdate(data))
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe(
                    { basketID = it.basketId },
                    { stateLiveData.value = AppState.Error(it) }
                )
        )
        else basketID?.let { basket ->
            compositeDisposable.add(
                basketInteractor.updateProduct(isLoggedUser, basket, ProductsUpdate(data))
                    .subscribeOn(schedulerProvider.io)
                    .observeOn(schedulerProvider.ui)
                    .subscribe({}, { stateLiveData.value = AppState.Error(it) })
            )
        }
        if (basketID == null) stateLiveData.value =
            AppState.Error(RuntimeException("Should init BasketID first"))
    }
}