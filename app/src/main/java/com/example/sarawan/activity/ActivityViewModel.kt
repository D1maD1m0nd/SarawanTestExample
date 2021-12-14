package com.example.sarawan.activity

import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.BaseViewModel
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.Basket
import com.example.sarawan.model.data.Query
import com.example.sarawan.rx.ISchedulerProvider
import javax.inject.Inject

class ActivityViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>() {

    private var basketID: Int? = null

    fun getBasket() {
        compositeDisposable.add(
            interactor.getData(Query.Get.Basket, true)
                .onErrorReturnItem(listOf(Basket()))
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .doOnSubscribe { stateLiveData.postValue(AppState.Loading) }
                .subscribe({
                    val basket = it.firstOrNull() as Basket?
                    basketID = basket?.basketId
                    val items = basket?.products as List<*>?
                    items?.let {
                        stateLiveData.postValue(AppState.Success(items))
                    }
                }, { stateLiveData.postValue(AppState.Error(it)) })
        )
    }
}