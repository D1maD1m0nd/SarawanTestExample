package ru.sarawan.android.framework.ui.basket.viewModel

import io.reactivex.rxjava3.observers.DisposableSingleObserver
import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.Basket
import ru.sarawan.android.model.data.basket.ProductInformation
import ru.sarawan.android.model.data.basket.ProductsItem
import ru.sarawan.android.model.data.product.ProductsUpdate
import ru.sarawan.android.model.interactor.BasketInteractor
import ru.sarawan.android.model.interactor.OrderInteractor
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.utils.MoshiAdapters.MoshiCustomAdapter
import javax.inject.Inject

class BasketViewModel @Inject constructor(
    private val basketInteractor: BasketInteractor,
    private val schedulerProvider: ISchedulerProvider,
    private val orderInteractor: OrderInteractor
) : BaseViewModel<AppState<*>>() {

    private var basketID: Int? = null

    private var products: List<ProductsItem> = mutableListOf()

    fun calculateOrder() {
        orderInteractor
            .calculateOrder(products)
            .subscribeOn(schedulerProvider.computation)
            .observeOn(schedulerProvider.ui)
            .subscribe(
                { stateLiveData.value = AppState.Success(it) },
                { stateLiveData.value = AppState.Error(it) })
    }


    fun getBasket(isLoggedUser: Boolean) {
        compositeDisposable.add(
            basketInteractor.getBasket(isLoggedUser)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .doOnSubscribe { stateLiveData.value = AppState.Loading }
                .subscribeWith(getObserverBasketList())
        )
    }


    fun clearBasket(isLoggedUser: Boolean) {
        basketID?.let {
            compositeDisposable.add(
                basketInteractor.clearBasket(isLoggedUser)
                    .subscribeOn(schedulerProvider.io)
                    .observeOn(schedulerProvider.ui)
                    .subscribe(
                        { stateLiveData.value = AppState.Empty },
                        { stateLiveData.value = AppState.Empty }),
            )
        }
        if (basketID == null) stateLiveData.value =
            AppState.Error(RuntimeException("Should init BasketID first"))
    }

    fun updateBasket(products: ProductsUpdate, isLoggedUser: Boolean) {
        basketID?.let { id ->
            compositeDisposable.add(
                basketInteractor.updateProduct(isLoggedUser, id, products)
                    .subscribeOn(schedulerProvider.io)
                    .observeOn(schedulerProvider.ui)
                    .subscribe(
                        { stateLiveData.value = AppState.Success(it) },
                        { stateLiveData.value = AppState.Success(emptyList<ProductsItem>()) })
            )
        }
        if (basketID == null) stateLiveData.value =
            AppState.Error(RuntimeException("Should init BasketID first"))
    }

    fun deleteBasketProduct(id: Int, isLoggedUser: Boolean) {
        compositeDisposable.add(
            basketInteractor.deleteProduct(isLoggedUser, id)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe(
                    { stateLiveData.value = AppState.Success(it) },
                    {}),
        )
    }

    private fun getObserverBasketList() = object : DisposableSingleObserver<Basket>() {
        override fun onSuccess(basket: Basket) {
            basketID = basket.basketId
            val items = basket.products
            if (items.isNullOrEmpty()) {
                stateLiveData.value = AppState.Empty
            } else {
                products = items
                stateLiveData.value = AppState.Success(items)
            }
        }

        override fun onError(e: Throwable) {
            stateLiveData.value = AppState.Success(emptyList<ProductsItem>())
        }
    }
}