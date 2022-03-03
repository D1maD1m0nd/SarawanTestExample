package ru.sarawan.android.framework.ui.profile.sms_code_fragment.viewModel

import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.UserRegistration
import ru.sarawan.android.model.interactor.UserInteractor
import ru.sarawan.android.rx.ISchedulerProvider
import javax.inject.Inject

class SmsCodeViewModel @Inject constructor(
    private val userInteractor: UserInteractor,
    private val schedulerProvider: ISchedulerProvider
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
}