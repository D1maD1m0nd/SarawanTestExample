package ru.sarawan.android.framework.ui.profile.viewModel

import ru.sarawan.android.framework.MainInteractor
import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.framework.ui.profile.interactor.IProfileInteractor
import ru.sarawan.android.model.data.AddressItem
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.Query
import ru.sarawan.android.model.data.UserDataModel
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.utils.constants.TypeCase
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val profileInteractor: IProfileInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>() {
    fun getUserData(id: Long) {
        compositeDisposable.addAll(
            interactor.getData(Query.Get.Users.UserData(id), true)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe(
                    { stateLiveData.value = AppState.Success(it) },
                    { stateLiveData.value = AppState.Error(it) }),
            interactor.getData(Query.Get.Address, true)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe(
                    { stateLiveData.value = AppState.Success(it) },
                    { stateLiveData.value = AppState.Error(it) }),
        )
    }

    fun getOrders() {
        compositeDisposable.add(
            interactor.getData(Query.Get.OrdersApproves, true)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe({ stateLiveData.postValue(AppState.Success(it)) },
                    { stateLiveData.value = AppState.Error(it) })
        )
    }

    fun deleteOrder(id: Int) {
        compositeDisposable.add(
            interactor.getData(Query.Delete.Order.Delete(id), true)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe({ stateLiveData.value = AppState.Success(it) },
                    { stateLiveData.value = AppState.Error(it) })
        )
    }

    fun getFormatAddress(address: AddressItem) {
        interactor.formatAddress(address)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .subscribe({ stateLiveData.value = AppState.Success(listOf(it), TypeCase.ADDRESS) },
                         { stateLiveData.value = AppState.Error(it) })
    }

    fun getFormatPhone(number: String, mask: String) {
        profileInteractor.formatPhone(number, mask)
            .subscribeOn(schedulerProvider.computation)
            .observeOn(schedulerProvider.ui)
            .subscribe(
                { stateLiveData.value = AppState.Success(listOf(it), TypeCase.FORMAT_PHONE) },
                { stateLiveData.value = AppState.Error(it) })
    }

    fun getFormatName(user: UserDataModel, defaultStr: String) {
        profileInteractor.formatName(user, defaultStr)
            .subscribeOn(schedulerProvider.computation)
            .observeOn(schedulerProvider.ui)
            .subscribe(
                { stateLiveData.value = AppState.Success(listOf(it), TypeCase.FORMAT_NAME) },
                { stateLiveData.value = AppState.Error(it) })
    }
}