package ru.sarawan.android.framework.ui.profile.viewModel

import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.AddressItem
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.OrderStatus
import ru.sarawan.android.model.data.UserDataModel
import ru.sarawan.android.model.interactor.OrderInteractor
import ru.sarawan.android.model.interactor.UserInteractor
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.utils.constants.TypeCase
import ru.sarawan.android.utils.formatAddress
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val userInteractor: UserInteractor,
    private val orderInteractor: OrderInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>() {
    fun getUserData(id: Long) {
        compositeDisposable.addAll(
            userInteractor.getUser(id)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe(
                    { stateLiveData.postValue(AppState.Success(it)) },
                    { stateLiveData.postValue(AppState.Error(it)) }),
            userInteractor.getAddress()
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe(
                    { stateLiveData.postValue(AppState.Success(it)) },
                    { stateLiveData.postValue(AppState.Error(it)) }),
        )
    }

    fun getOrders() {
        compositeDisposable.add(
            orderInteractor.getOrders()
                .map { it.filter { orderApprove -> orderApprove.orderStatus != OrderStatus.CAN } }
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe(
                    { stateLiveData.postValue(AppState.Success(it)) },
                    { stateLiveData.postValue(AppState.Error(it)) }
                )
        )
    }

    fun deleteOrder(id: Int) {
        compositeDisposable.add(
            orderInteractor.cancelOrder(id)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe({}, { stateLiveData.postValue(AppState.Error(it)) })
        )
    }

    fun getFormatAddress(address: AddressItem) {
        stateLiveData.value = AppState.Success(formatAddress(address))
    }

    fun getFormatPhone(number: String, mask: String) {
        userInteractor
            .formatPhone(number, mask)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io)
            .subscribe(
                { stateLiveData.postValue(AppState.Success(it, TypeCase.FORMAT_PHONE)) },
                { stateLiveData.postValue(AppState.Error(it)) })
    }

    fun getFormatName(user: UserDataModel, defaultStr: String) {
        userInteractor.formatName(user, defaultStr)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io)
            .subscribe(
                { stateLiveData.postValue(AppState.Success(it, TypeCase.FORMAT_NAME)) },
                { stateLiveData.postValue(AppState.Error(it)) })
    }
}