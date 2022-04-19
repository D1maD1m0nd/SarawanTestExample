package ru.sarawan.android.activity

import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.basket.ProductsItem
import ru.sarawan.android.model.data.product.Product
import ru.sarawan.android.model.data.product.ProductsUpdate
import ru.sarawan.android.model.interactor.BasketInteractor
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.utils.NetworkStatus
import ru.sarawan.android.utils.exstentions.localstore.LocalStore
import ru.sarawan.android.utils.exstentions.localstore.UNREGISTERED
import javax.inject.Inject

class ActivityViewModel @Inject constructor(
    private val basketInteractor: BasketInteractor,
    private val schedulerProvider: ISchedulerProvider,
    private val networkStatus: NetworkStatus,
    private val sharedPreferences: LocalStore
) : BaseViewModel<AppState<*>>() {

    fun getBasket() {
        compositeDisposable.clear()
        compositeDisposable.add(
            basketInteractor.getBasket(!sharedPreferences.token.isNullOrEmpty())
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe(
                    { stateLiveData.postValue(AppState.Success(it.products)) },
                    { stateLiveData.postValue(AppState.Error(it)) }
                )
        )
    }

    fun initNetwork() {
        networkStatus
            .isOnline()
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io)
            .subscribe { this.getBasket() }
    }

    fun saveData(data: List<ProductsItem>, isLoggedUser: Boolean) {
        if (sharedPreferences.userId != UNREGISTERED && data.isNotEmpty()) {
            val products = data.map { item ->
                Product(id = item.basketProduct?.basketProduct?.id, quantity = item.quantity ?: 0)
            }
            basketInteractor.putProduct(isLoggedUser, ProductsUpdate(products))
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe({}, { stateLiveData.postValue(AppState.Error(it)) })
        }
    }
}