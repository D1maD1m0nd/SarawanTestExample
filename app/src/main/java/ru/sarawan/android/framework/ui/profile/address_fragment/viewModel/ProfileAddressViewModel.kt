package ru.sarawan.android.framework.ui.profile.address_fragment.viewModel

import ru.sarawan.android.framework.MainInteractor
import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.AddressItem
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.Query
import ru.sarawan.android.rx.ISchedulerProvider
import javax.inject.Inject

class ProfileAddressViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>()  {
    fun createAddress(address : AddressItem) {
        compositeDisposable.add(
            interactor.getData(Query.Post.Address.NewAddress(address), true)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe(
                    {stateLiveData.postValue(AppState.Success(it))},
                    { stateLiveData.postValue(AppState.Error(it)) })
        )
    }
}