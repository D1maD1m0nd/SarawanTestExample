package ru.sarawan.android.framework.ui.profile.name_fragment.viewModel

import ru.sarawan.android.framework.MainInteractor
import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.Query
import ru.sarawan.android.model.data.UserDataModel
import ru.sarawan.android.rx.ISchedulerProvider
import javax.inject.Inject

class NameViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>() {
    fun updateUser(user: UserDataModel, id: Long) {
        compositeDisposable.add(
            interactor.getData(Query.Put.Users.Update(id, user), true)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe(
                    { stateLiveData.postValue(AppState.Success(it)) },
                    { stateLiveData.postValue(AppState.Error(it)) })
        )
    }
}