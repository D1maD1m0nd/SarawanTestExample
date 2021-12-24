package com.example.sarawan.framework.ui.order.viewModel

import androidx.lifecycle.MutableLiveData
import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.BaseViewModel
import com.example.sarawan.model.data.*
import com.example.sarawan.rx.ISchedulerProvider
import javax.inject.Inject

class OrderViewModel@Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>() {
    fun getAddress() {
        compositeDisposable.addAll(
            interactor.getData(Query.Get.Address, true)
                .map { list ->
                    list as List<AddressItem>
                    listOf(list.findLast {
                        it.primary == true
                    })
                }
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .doOnSubscribe { stateLiveData.postValue(AppState.Loading) }
                .subscribe(
                    {stateLiveData.postValue(AppState.Success(it))},
                    { stateLiveData.postValue(AppState.Error(it)) })
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
}
