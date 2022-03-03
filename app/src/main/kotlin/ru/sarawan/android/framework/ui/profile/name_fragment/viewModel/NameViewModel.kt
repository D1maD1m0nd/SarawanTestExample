package ru.sarawan.android.framework.ui.profile.name_fragment.viewModel

import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.UserDataModel
import ru.sarawan.android.model.interactor.UserInteractor
import ru.sarawan.android.rx.ISchedulerProvider
import javax.inject.Inject

class NameViewModel @Inject constructor(
    private val userInteractor: UserInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>() {
    fun updateUser(user: UserDataModel, id: Long) {
        compositeDisposable.add(
            userInteractor.updateUser(id, user)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe(
                    { stateLiveData.postValue(AppState.Success(it)) },
                    { stateLiveData.postValue(AppState.Error(it)) })
        )
    }
}