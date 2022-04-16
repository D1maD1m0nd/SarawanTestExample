package ru.sarawan.android.framework.ui.profile.viewModel

import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.address.sarawan.AddressItem
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.OrderStatus
import ru.sarawan.android.model.data.UserDataModel
import ru.sarawan.android.model.interactor.OrderInteractor
import ru.sarawan.android.model.interactor.UserInteractor
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.utils.constants.TypeCase

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
                .observeOn(schedulerProvider.ui)
                .subscribe(
                    { stateLiveData.value = AppState.Success(it) },
                    { stateLiveData.value = AppState.Error(it) }),
            userInteractor.getAddress()
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe(
                    { stateLiveData.value = AppState.Success(it) },
                    { stateLiveData.value = AppState.Error(it) }),
        )
    }

    fun getOrders() {
        compositeDisposable.add(
            orderInteractor.getOrders()
                .map {
                    it.filter { orderApprove ->
                        orderApprove.orderStatus != OrderStatus.CAN
                    }
                }
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe(
                    { stateLiveData.value = AppState.Success(it) },
                    { stateLiveData.value = AppState.Error(it) }
                )
        )
    }

    fun deleteOrder(id: Int) {
        compositeDisposable.add(
            orderInteractor.cancelOrder(id)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe({}, { stateLiveData.value = AppState.Error(it) })
        )
    }

    fun getFormatAddress(address: AddressItem) {
        compositeDisposable.add(
            userInteractor.formatAddress(address)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe(
                    { stateLiveData.value = AppState.Success(it, TypeCase.ADDRESS) },
                    { stateLiveData.value = AppState.Error(it) })
        )
    }

    fun getFormatPhone(number: String, mask: String) {
        compositeDisposable.add(
            userInteractor
                .formatPhone(number, mask)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe(
                    { stateLiveData.value = AppState.Success(it, TypeCase.FORMAT_PHONE) },
                    { stateLiveData.value = AppState.Error(it) })
        )

    }

    fun getFormatName(user: UserDataModel, defaultStr: String) {
        compositeDisposable.add(
            userInteractor.formatName(user, defaultStr)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe(
                    { stateLiveData.value = AppState.Success(it, TypeCase.FORMAT_NAME) },
                    { stateLiveData.value = AppState.Error(it) })
        )
    }
}