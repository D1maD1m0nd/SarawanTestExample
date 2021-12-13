package com.example.sarawan.framework.ui.product_card.viewModel

import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.BaseViewModel
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.Basket
import com.example.sarawan.model.data.ProductsUpdate
import com.example.sarawan.model.data.Query
import com.example.sarawan.rx.ISchedulerProvider
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import javax.inject.Inject

class ProductCardViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>(){
    private var basketID: Int? = null
    fun getProduct(storeId : Long?) {
        storeId?.let {
            compositeDisposable.addAll(
                interactor.getData(Query.Get.Products.Id(storeId), true)
                    .subscribeOn(schedulerProvider.io)
                    .observeOn(schedulerProvider.io)
                    .observeOn(schedulerProvider.io)
                    .subscribe(
                        {stateLiveData.postValue(AppState.Success(it))},
                        { stateLiveData.postValue(AppState.Error(it)) }),
                interactor.getData(Query.Get.Products.SimilarProducts(storeId), true)
                    .subscribeOn(schedulerProvider.io)
                    .observeOn(schedulerProvider.io)
                    .observeOn(schedulerProvider.io)
                    .subscribe(
                        {stateLiveData.postValue(AppState.Success(it))},
                        { stateLiveData.postValue(AppState.Error(it)) }),
            )

        }
    }

    fun updateBasket(products: ProductsUpdate) {
        basketID?.let { id ->
            compositeDisposable.add(
                interactor.getData(Query.Put.Basket.Update(id, products), true)
                    .subscribeOn(schedulerProvider.io)
                    .observeOn(schedulerProvider.io)
                    .doOnSubscribe { stateLiveData.postValue(AppState.Loading) }
                    .subscribeWith(getObserver())
            )
        }
        if (basketID == null) stateLiveData.value =
            AppState.Error(RuntimeException("Should init BasketID first"))
    }
    private fun getObserver() = object : DisposableSingleObserver<List<*>>() {
        override fun onSuccess(result: List<*>) {
            val basket = result.first() as Basket
            basketID = basket.basketId
            val items = basket.products as List<*>
            stateLiveData.postValue(AppState.Success(items))
        }

        override fun onError(e: Throwable) {
            stateLiveData.postValue(AppState.Error(e))
        }
    }
}