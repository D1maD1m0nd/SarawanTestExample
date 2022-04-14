package ru.sarawan.android.framework.ui.profile.sms_code_fragment.viewModel

import android.util.Log
import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.UserRegistration
import ru.sarawan.android.model.interactor.UserInteractor
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.service.IncomingCallReaderService
import ru.sarawan.android.service.contacts.ReceiveMessage
import javax.inject.Inject

class SmsCodeViewModel @Inject constructor(
    private val userInteractor: UserInteractor,
    private val schedulerProvider: ISchedulerProvider,
    private val incomingCallReaderService: ReceiveMessage
) : BaseViewModel<AppState<*>>() {
    fun createUser(user: UserRegistration) {
        compositeDisposable.addAll(
            userInteractor.createUser(user)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe(
                    { stateLiveData.postValue(AppState.Success(it)) },
                    { stateLiveData.postValue(AppState.Error(it)) })
        )
    }

    fun getPhoneNumber(){
        compositeDisposable.addAll(
            incomingCallReaderService.getMessage()
                .subscribeOn(schedulerProvider.io)
                ?.observeOn(schedulerProvider.ui)
                ?.subscribe(
                    {stateLiveData.postValue(AppState.Success(it)) },
                    { stateLiveData.postValue(AppState.Error(it))}
                )
        )
    }
}