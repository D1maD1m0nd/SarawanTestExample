package ru.sarawan.android.framework.ui.basket.viewModel

import io.reactivex.rxjava3.observers.DisposableSingleObserver
import ru.sarawan.android.framework.MainInteractor
import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.framework.ui.basket.interactor.IBasketInteractor
import ru.sarawan.android.model.data.*
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.utils.MoshiCustomAdapter
import javax.inject.Inject

class BasketViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider,
    private val moshiCustomAdapter: MoshiCustomAdapter,
    private val basketInteractor: IBasketInteractor
) : BaseViewModel<AppState<*>>() {

    private var basketID: Int? = null

    private var products: List<ProductsItem> = mutableListOf()

    fun calculateOrder() {
        basketInteractor
            .calculateOrder(products)
            .subscribeOn(schedulerProvider.computation)
            .observeOn(schedulerProvider.ui)
            .subscribe(
                { stateLiveData.value = AppState.Success(listOf(it)) },
                { stateLiveData.value = AppState.Error(it) })
    }


    fun getBasket(isLoggedUser: Boolean) {
        compositeDisposable.add(
            interactor.getData(Query.Get.Basket, isLoggedUser)
                .map {
                    if (it.isNotEmpty()) {
                        val basket = it.first() as Basket
                        basket.products?.map { item ->
                            var info: ProductInformation? = null
                            val json =
                                item.basketProduct?.basketProduct?.information?.productInformation
                            json?.let { newJson ->
                                info = moshiCustomAdapter.infoFromJson(newJson)
                            }
                            item.basketProduct?.basketProduct?.information = info
                            it
                        }
                    }
                    it
                }
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .doOnSubscribe { stateLiveData.value = AppState.Loading }
                .subscribeWith(getObserverBasketList())
        )
    }


    fun clearBasket(isLoggedUser: Boolean) {
        basketID?.let {
            compositeDisposable.add(
                interactor.getData(Query.Delete.Basket.Clear, isLoggedUser)
                    .subscribeOn(schedulerProvider.io)
                    .observeOn(schedulerProvider.ui)
                    .subscribe(
                        { stateLiveData.value = AppState.Empty },
                        { stateLiveData.value = AppState.Empty }),
            )
        }
        if (basketID == null) stateLiveData.value =
            AppState.Error(RuntimeException("Should init BasketID first"))
    }

    fun updateBasket(products: ProductsUpdate, isLoggedUser: Boolean) {
        basketID?.let { id ->
            compositeDisposable.add(
                interactor.getData(Query.Put.Basket.Update(id, products), isLoggedUser)
                    .subscribeOn(schedulerProvider.io)
                    .observeOn(schedulerProvider.ui)
                    .subscribe(
                        { stateLiveData.value = AppState.Success(it) },
                        { stateLiveData.value = AppState.Success(emptyList<ProductsItem>()) })
            )
        }
        if (basketID == null) stateLiveData.value =
            AppState.Error(RuntimeException("Should init BasketID first"))
    }

    fun deleteBasketProduct(id: Int, isLoggedUser: Boolean) {
        compositeDisposable.add(
            interactor.getData(Query.Delete.Basket.Remove(id), isLoggedUser)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe(
                    { stateLiveData.value = AppState.Success(it) },
                    {}),
        )
    }

    private fun getObserverBasketList() = object : DisposableSingleObserver<List<*>>() {
        override fun onSuccess(result: List<*>) {
            val basket = result.first() as Basket
            basketID = basket.basketId
            val items = basket.products as List<*>
            if (items.isEmpty()) {
                stateLiveData.postValue(AppState.Empty)
            } else {
                products = items as List<ProductsItem>
                stateLiveData.value = AppState.Success(items)
            }
        }

        override fun onError(e: Throwable) {
            stateLiveData.value = AppState.Success(emptyList<ProductsItem>())
        }
    }
}