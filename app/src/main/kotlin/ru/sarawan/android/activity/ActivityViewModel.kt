package ru.sarawan.android.activity

import android.content.SharedPreferences
import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.Product
import ru.sarawan.android.model.data.ProductsUpdate
import ru.sarawan.android.model.interactor.BasketInteractor
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.utils.NetworkStatus
import ru.sarawan.android.utils.exstentions.token
import javax.inject.Inject

class ActivityViewModel @Inject constructor(
    private val basketInteractor: BasketInteractor,
    private val schedulerProvider: ISchedulerProvider,
    private val networkStatus: NetworkStatus,
    private val sharedPreferences: SharedPreferences
) : BaseViewModel<AppState<*>>() {

    fun getBasket(isLoggedUser: Boolean) {
        compositeDisposable.clear()
        compositeDisposable.add(
            basketInteractor.getBasket(isLoggedUser)
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
            .subscribe { this.getBasket(!sharedPreferences.token.isNullOrEmpty()) }
    }

    fun saveData(data: List<Product>, isLoggedUser: Boolean) {
        basketInteractor.putProduct(isLoggedUser, ProductsUpdate(data))
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io)
            .subscribe({}, { stateLiveData.postValue(AppState.Error(it)) })
    }
}