package ru.sarawan.android.activity


import ru.sarawan.android.framework.MainInteractor
import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.*
import ru.sarawan.android.rx.ISchedulerProvider
import javax.inject.Inject

class ActivityViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>() {

    fun getBasket(isLoggedUser: Boolean) {
        compositeDisposable.clear()
        compositeDisposable.add(
            interactor.getData(Query.Get.Basket, isLoggedUser)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe({ baskets ->
                    val items = (baskets.firstOrNull() as? Basket)?.products
                    items?.let {
                        stateLiveData.postValue(AppState.Success(it))
                    }
                }, { stateLiveData.postValue(AppState.Error(it)) })
        )
    }

    fun saveData(data: List<Product>, isLoggedUser: Boolean) {
        interactor
                .getData(Query.Post.Basket.Put(ProductsUpdate(data)), isLoggedUser)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe({}, { stateLiveData.postValue(AppState.Error(it)) })
    }
}