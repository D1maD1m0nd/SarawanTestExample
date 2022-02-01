package ru.sarawan.android.framework.ui.profile.phone_fragment.viewModel

import ru.sarawan.android.framework.MainInteractor
import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.Query
import ru.sarawan.android.model.data.UserRegistration
import ru.sarawan.android.rx.ISchedulerProvider
import javax.inject.Inject

class ProfilePhoneViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>() {
    fun sendSms(user : UserRegistration) {
            compositeDisposable.addAll(
                interactor.getData(Query.Post.User.Sms(user), true)
                    .subscribeOn(schedulerProvider.io)
                    .observeOn(schedulerProvider.ui)
                    .subscribe(
                        {stateLiveData.postValue(AppState.Success(it))},
                        { stateLiveData.postValue(AppState.Error(it)) }),
            )
    }

}