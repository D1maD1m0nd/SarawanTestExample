package com.example.sarawan.framework.ui.profile.sms_code_fragment.viewModel

import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.BaseViewModel
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.Query
import com.example.sarawan.model.data.UserRegistration
import com.example.sarawan.rx.ISchedulerProvider
import javax.inject.Inject

class SmsCodeViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>() {
    fun createUser(user: UserRegistration){
        compositeDisposable.addAll(
            interactor.getData(Query.Post.User.NewUser(user), true)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe(
                    {stateLiveData.postValue(AppState.Success(it))},
                    { stateLiveData.postValue(AppState.Error(it)) }),
        )
    }
}