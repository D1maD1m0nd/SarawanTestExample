package ru.sarawan.android.framework.ui.profile.address_fragment.viewModel

import ru.sarawan.android.framework.MainInteractor
import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.framework.ui.profile.address_fragment.interactor.AddressInteractor
import ru.sarawan.android.framework.ui.profile.address_fragment.interactor.IAddressInteractor
import ru.sarawan.android.model.data.AddressItem
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.Query
import ru.sarawan.android.rx.ISchedulerProvider
import javax.inject.Inject

class ProfileAddressViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val addressInteractor: IAddressInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>() {
    fun createAddress(address: AddressItem) {
        compositeDisposable.add(
            interactor.getData(Query.Post.Address.NewAddress(address), true)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe(
                    { stateLiveData.value = AppState.Success(it) },
                    { stateLiveData.value = AppState.Error(it) })

        )
    }

    fun validateAddress(address: AddressItem) {
        addressInteractor.validateAddress(address)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.ui)
            .subscribe(
                { stateLiveData.value = AppState.Success(listOf(it)) },
                { stateLiveData.value = AppState.Error(it) })
    }
}