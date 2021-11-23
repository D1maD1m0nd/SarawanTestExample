package com.example.sarawan.framework.ui.basket.viewModel

import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.BaseViewModel
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.Query
import com.example.sarawan.rx.ISchedulerProvider
import javax.inject.Inject

class BasketViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>() {

    fun getBasket() {
        compositeDisposable.add(
            interactor.getData(Query.Get.Basket, true)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .doOnSubscribe { stateLiveData.postValue(AppState.Loading) }
                .subscribe({
                    stateLiveData.postValue(AppState.Success(it))
                }, {
                    stateLiveData.postValue(AppState.Error(it))
                })
        )
    }
}