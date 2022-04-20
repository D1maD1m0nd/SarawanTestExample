package ru.sarawan.android.framework.ui.profile.sms_code_fragment.viewModel

import android.util.Log
import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.UserRegistration
import ru.sarawan.android.model.interactor.UserInteractor
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.service.contacts.ReceiveMessage
import javax.inject.Inject

class SmsCodeViewModel @Inject constructor(
    private val userInteractor: UserInteractor,
    private val schedulerProvider: ISchedulerProvider,
    private val incomingCallReaderService: ReceiveMessage
) : BaseViewModel<AppState<*>>() {
    fun createUser(user: UserRegistration) {
        compositeDisposable.add(
            userInteractor.createUser(user)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe(
                    { stateLiveData.value = AppState.Success(it) },
                    { stateLiveData.value = AppState.Error(it) })
        )
    }

    fun getPhoneNumber() {
        compositeDisposable.add(
            incomingCallReaderService.getMessage()
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe(
                    {
                        Log.e("phone", it)
                        stateLiveData.value = AppState.Success(it)
                    },
                    {
                        Log.e("phone", it.message.toString())
                        stateLiveData.value = AppState.Error(it)
                    }
                )
        )
    }
}