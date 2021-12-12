package com.example.sarawan.framework.ui.profile.address_fragment.viewModel

import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.BaseViewModel
import com.example.sarawan.model.data.AddressItem
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.Query
import com.example.sarawan.rx.ISchedulerProvider
import javax.inject.Inject

class ProfileAddressViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>()  {
    fun createAddress(address : AddressItem) {
        compositeDisposable.add(
            interactor.getData(Query.Post.Address.NewAddress(address), true)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe(
                    {stateLiveData.postValue(AppState.Success(it))},
                    { stateLiveData.postValue(AppState.Error(it)) })
        )
    }
}