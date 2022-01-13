package ru.sarawan.android.framework.ui.order.viewModel

import ru.sarawan.android.framework.MainInteractor
import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.AddressItem
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.Query
import ru.sarawan.android.rx.ISchedulerProvider
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
                    var adress = list.find {
                        it.primary == true
                    }
                    if(adress == null) {
                        adress = list.firstOrNull()
                    }
                    listOf(adress)
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
