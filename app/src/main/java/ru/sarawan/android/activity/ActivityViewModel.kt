package ru.sarawan.android.activity


import ru.sarawan.android.framework.MainInteractor
import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.Basket
import ru.sarawan.android.model.data.Query
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
                    val price = items?.sumOf {
                        var result = 0.0
                        it.quantity?.let { quantity ->
                            it.basketProduct?.price?.let { price ->
                               result += (price.toDouble() * quantity)
                            }
                        }
                        result
                    }?.toFloat()
                    stateLiveData.postValue(AppState.Success(listOf(price)))
                }, { stateLiveData.postValue(AppState.Error(it)) })
        )
    }
}