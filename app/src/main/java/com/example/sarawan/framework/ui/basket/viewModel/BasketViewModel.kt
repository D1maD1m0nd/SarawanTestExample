package com.example.sarawan.framework.ui.basket.viewModel

import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.BaseViewModel
import com.example.sarawan.model.data.*
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
                .subscribeWith(getObserverBasketList())
        )
    }

    fun getAddress() {
        compositeDisposable.add(
            interactor.getData(Query.Get.Address, true)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe(
                    { stateLiveData.postValue(AppState.Success(it)) },
                    { stateLiveData.postValue(AppState.Error(it)) }),
        )
    }

    fun getOrder(address: AddressItem) {
        compositeDisposable.add(
            interactor.getData(Query.Get.Orders.Order(address), true)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe(
                    { stateLiveData.postValue(AppState.Success(it)) },
                    { stateLiveData.postValue(AppState.Error(it)) }),
        )
    }

    fun createOrder(address: AddressItem){
        basketID?.let { id ->
            compositeDisposable.add(
                interactor.getData(Query.Post.Order.Create(address), true)
                    .subscribeOn(schedulerProvider.io)
                    .observeOn(schedulerProvider.io)
                    .doOnSubscribe { stateLiveData.postValue(AppState.Loading) }
                    .subscribe(
                        {stateLiveData.postValue(AppState.Success(it))},
                        { stateLiveData.postValue(AppState.Error(it)) }),
            )
        }
        if (basketID == null) stateLiveData.value =
            AppState.Error(RuntimeException("Should init BasketID first"))
    }

    fun clearBasket() {
        basketID?.let { id ->
            compositeDisposable.add(
                interactor.getData(Query.Delete.Basket.Clear, true)
                    .subscribeOn(schedulerProvider.io)
                    .observeOn(schedulerProvider.io)
                    .subscribe(
                        {stateLiveData.postValue(AppState.Empty)},
                        {stateLiveData.postValue(AppState.Empty)}),
            )
        }
        if (basketID == null) stateLiveData.value =
            AppState.Error(RuntimeException("Should init BasketID first"))
    }

    fun updateBasket(products: ProductsUpdate) {
        basketID?.let { id ->
            compositeDisposable.add(
                interactor.getData(Query.Put.Basket.Update(id, products), true)
                    .subscribeOn(schedulerProvider.io)
                    .observeOn(schedulerProvider.io)
                    .subscribeWith(getObserver())
            )
        }
        if (basketID == null) stateLiveData.value =
            AppState.Error(RuntimeException("Should init BasketID first"))
    }

    fun deleteBasketProduct(id: Int) {
        compositeDisposable.add(
            interactor.getData(Query.Delete.Basket.Remove(id), true)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
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
//            stateLiveData.postValue(AppState.Error(e))
            stateLiveData.postValue(AppState.Success(emptyList<ProductsItem>()))
        }
    }

    private fun getObserverBasketList() = object : DisposableSingleObserver<List<*>>() {
        override fun onSuccess(result: List<*>) {
            val basket = result.first() as Basket
            basketID = basket.basketId
            val items = basket.products as List<*>
            if(items.isEmpty()) {
                stateLiveData.postValue(AppState.Empty)
            } else {
                stateLiveData.postValue(AppState.Success(items))
            }
        }

        override fun onError(e: Throwable) {
//            stateLiveData.postValue(AppState.Error(e))
            stateLiveData.postValue(AppState.Success(emptyList<ProductsItem>()))
        }
    }
}