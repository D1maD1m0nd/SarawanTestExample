package ru.sarawan.android.framework.ui.profile.address_fragment.viewModel

import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.address.sarawan.AddressItem
import ru.sarawan.android.model.interactor.UserInteractor
import ru.sarawan.android.rx.ISchedulerProvider
import javax.inject.Inject

class ProfileAddressViewModel @Inject constructor(
    private val userInteractor: UserInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>() {
    fun createAddress(address: AddressItem) {
        compositeDisposable.add(
            userInteractor.createAddress(address)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe(
                    { stateLiveData.value = AppState.Success(it) },
                    { stateLiveData.value = AppState.Error(it) })

        )
    }

    fun validateAddress(address: AddressItem) {
        userInteractor.validateAddress(address)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .subscribe(
                { stateLiveData.value = AppState.Success(it) },
                { stateLiveData.value = AppState.Error(it) })
    }


}