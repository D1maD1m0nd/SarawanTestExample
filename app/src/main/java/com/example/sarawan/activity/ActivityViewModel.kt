package com.example.sarawan.activity

import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.BaseViewModel
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.Basket
import com.example.sarawan.model.data.Query
import com.example.sarawan.rx.ISchedulerProvider
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import javax.inject.Inject

class ActivityViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>() {

    private var basketID: Int? = null

    fun getBasket() {
        compositeDisposable.add(
            interactor.getData(Query.Get.Basket, true)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .doOnSubscribe { stateLiveData.postValue(AppState.Loading) }
                .subscribeWith(getObserver())
        )
    }

    private fun getObserver() = object : DisposableSingleObserver<List<*>>() {
        override fun onSuccess(result: List<*>) {
            val basket = result.first() as Basket
            basketID = basket.basketId
            val items = basket.products as List<*>
            stateLiveData.postValue(AppState.Success(items))
        }

        override fun onError(e: Throwable) {
            stateLiveData.value = AppState.Error(e)
        }
    }
}