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

    fun getBasket() {
        compositeDisposable.clear()
        compositeDisposable.add(
            interactor.getData(Query.Get.Basket, true)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe({ baskets ->
                    val items = (baskets.firstOrNull() as? Basket)?.products
                    val price = items?.sumOf {
                        var result = 0.0
                        it.quantity?.let { quantity ->
                            it.basketProduct?.price?.let { price ->
                               result += (price.toDouble() * quantity)
                            }
                        }
                        result
                    }?.toFloat()
                    stateLiveData.postValue(AppState.Success(listOf(price)))
                }, { stateLiveData.postValue(AppState.Error(it)) })
        )
    }
}