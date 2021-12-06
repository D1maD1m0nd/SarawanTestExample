package com.example.sarawan.framework.ui.profile.name_fragment.viewModel

import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.BaseViewModel
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.Query
import com.example.sarawan.model.data.UserDataModel
import com.example.sarawan.rx.ISchedulerProvider
import javax.inject.Inject

class NameViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>() {
    fun updateUser(user : UserDataModel, id : Long) {
        compositeDisposable.add(
            interactor.getData(Query.Put.Users.Update(id, user), true)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe(
                    {stateLiveData.postValue(AppState.Success(it))},
                    { stateLiveData.postValue(AppState.Error(it)) })
        )
    }
}