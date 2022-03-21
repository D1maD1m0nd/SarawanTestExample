package ru.sarawan.android.framework.ui.order.viewModel

import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.AddressItem
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.interactor.OrderInteractor
import ru.sarawan.android.model.interactor.UserInteractor
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.utils.constants.TypeCase
import javax.inject.Inject

class OrderViewModel @Inject constructor(
    private val userInteractor: UserInteractor,
    private val orderInteractor: OrderInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>() {
    fun getFormatAddress(address: AddressItem) {
        compositeDisposable.add(
            userInteractor.formatAddress(address)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe({stateLiveData.value = AppState.Success(it, TypeCase.ADDRESS)},
                    { stateLiveData.value = AppState.Error(it) })
        )
    }

    fun getAddress() {
        compositeDisposable.addAll(
            userInteractor.getAddress()
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .doOnSubscribe { stateLiveData.value = AppState.Loading }
                .subscribe({ addressItems ->
                    val address = addressItems.find { it.primary } ?: addressItems.firstOrNull()
                    stateLiveData.value = AppState.Success(address) },
                    { stateLiveData.value = AppState.Error(it) })
        )
    }

    fun getOrder(address: AddressItem) {
        compositeDisposable.add(
            orderInteractor.getPreCalculationOrder(address)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe(
                    { stateLiveData.value = AppState.Success(it) },
                    { stateLiveData.value = AppState.Error(it) }),
        )
    }

    fun createOrder(address: AddressItem) {
        compositeDisposable.add(
            orderInteractor.createOrder(address)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .doOnSubscribe { stateLiveData.value = AppState.Loading }
                .subscribe(
                    { stateLiveData.value = AppState.Success(it) },
                    { stateLiveData.value = AppState.Error(it) }),
        )
    }
}
