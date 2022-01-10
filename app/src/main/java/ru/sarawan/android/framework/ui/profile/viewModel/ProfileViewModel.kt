package ru.sarawan.android.framework.ui.profile.viewModel

import ru.sarawan.android.framework.MainInteractor
import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.Query
import ru.sarawan.android.rx.ISchedulerProvider
import javax.inject.Inject

class ProfileViewModel  @Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>(){
    fun getUserData(id : Long) {
        compositeDisposable.addAll(
            interactor.getData(Query.Get.Users.UserData(id), true)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe(
                    {stateLiveData.postValue(AppState.Success(it))},
                    { stateLiveData.postValue(AppState.Error(it)) }),
            interactor.getData(Query.Get.Address, true)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe(
                    {stateLiveData.postValue(AppState.Success(it))},
                    { stateLiveData.postValue(AppState.Error(it)) }),
        )
    }

    fun getOrders() {
        compositeDisposable.add(
            interactor.getData(Query.Get.OrdersApproves, true)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe({stateLiveData.postValue(AppState.Success(it))},
                            {stateLiveData.postValue(AppState.Error(it)) })
        )
    }

    fun deleteOrder(id : Int) {
        compositeDisposable.add(
            interactor.getData(Query.Delete.Order.Delete(id), true)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe({stateLiveData.postValue(AppState.Success(it))},
                    {stateLiveData.postValue(AppState.Error(it)) })
        )
    }

}