package com.example.sarawan.framework.ui.basket.viewModel

import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.BaseViewModel
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.Basket
import com.example.sarawan.model.data.ProductsUpdate
import com.example.sarawan.model.data.Query
import com.example.sarawan.rx.ISchedulerProvider
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import javax.inject.Inject

class BasketViewModel @Inject constructor(
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

    fun updateBasket(products: ProductsUpdate) {
        basketID?.let { id ->
            compositeDisposable.add(
                interactor.getData(Query.Put.Basket.Update(id, products), true)
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
        if (basketID == null) stateLiveData.value =
            AppState.Error(RuntimeException("Should init BasketID first"))
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