package ru.sarawan.android.framework.ui.order.viewModel

import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.AddressItem
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.interactor.OrderInteractor
import ru.sarawan.android.model.interactor.UserInteractor
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.utils.formatAddress
import javax.inject.Inject

class OrderViewModel @Inject constructor(
    private val userInteractor: UserInteractor,
    private val orderInteractor: OrderInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>() {
    fun getFormatAddress(address: AddressItem) {
        stateLiveData.value = AppState.Success(listOf(formatAddress(address)))
    }

    fun getAddress() {
        compositeDisposable.addAll(
            userInteractor.getAddress()
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .doOnSubscribe { stateLiveData.postValue(AppState.Loading) }
                .subscribe({ addressItems ->
                    val address = addressItems.find { it.primary } ?: addressItems.firstOrNull()
                    stateLiveData.postValue(AppState.Success(address))
                }, { stateLiveData.postValue(AppState.Error(it)) })
        )
    }

    fun getOrder(address: AddressItem) {
        compositeDisposable.add(
            orderInteractor.getPreCalculationOrder(address)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe(
                    { stateLiveData.postValue(AppState.Success(it)) },
                    { stateLiveData.postValue(AppState.Error(it)) }),
        )
    }

    fun createOrder(address: AddressItem) {
        compositeDisposable.add(
            orderInteractor.createOrder(address)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .doOnSubscribe { stateLiveData.postValue(AppState.Loading) }
                .subscribe(
                    { stateLiveData.postValue(AppState.Success(it)) },
                    { stateLiveData.postValue(AppState.Error(it)) }),
        )
    }
}
