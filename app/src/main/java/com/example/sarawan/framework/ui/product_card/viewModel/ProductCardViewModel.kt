package com.example.sarawan.framework.ui.product_card.viewModel

import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.BaseViewModel
import com.example.sarawan.model.data.*
import com.example.sarawan.rx.ISchedulerProvider
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import javax.inject.Inject

class ProductCardViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>(){
    private var basketID: Int? = null
    fun clear() {
        stateLiveData.value = AppState.Empty
    }
    fun similarProducts(storeId : Long?) {
        storeId?.let { store ->
            val basket =  interactor.getData(Query.Get.Basket, true).onErrorReturnItem(listOf(Basket()))
            val similar = interactor.getData(Query.Get.Products.SimilarProducts(store), true)
            val product = interactor.getData(Query.Get.Products.Id(storeId), true)
            compositeDisposable.add(
                Single.zip(similar, basket, product) {similarData, basketData, productData->
                    productData as MutableList<Product>
                    basketData as MutableList<Basket>
                    similarData as MutableList<Product>
                    similarData.addAll(productData)

                    var data: List<Product>
                    val basketObject = (basketData as List<Basket>).firstOrNull()
                    basketID = basketObject?.basketId
                    if(basketData.isNotEmpty()) {
                        data = similarData.map { similarProduct ->
                            similarProduct.storePrices?.sortBy { storeItem ->
                                storeItem.price
                            }
                            basketObject?.products?.forEach { basketSingleData ->
                                val similarEq = similarProduct.id == basketSingleData.basketProduct?.basketProduct?.id
                                if (similarEq) {
                                    similarProduct.count = basketSingleData.quantity!!
                                    val storeIdProduct = basketSingleData.basketProduct?.productStoreId
                                    storeIdProduct?.let { idProduct ->
                                        similarProduct
                                            .storePrices
                                            ?.findLast { it.id == idProduct }
                                            ?.let {
                                                it.count = basketSingleData.quantity!!
                                            }
                                    }
                                }
                            }
                            similarProduct
                        }

                    } else {
                        data = similarData.map { similarProduct ->
                            similarProduct.storePrices?.sortBy { storeItem ->
                                storeItem.price
                            }
                            similarProduct
                        }
                    }
                    data
                }
                    .subscribeOn(schedulerProvider.io)
                    .observeOn(schedulerProvider.ui)
                    .subscribe(
                        { stateLiveData.postValue(AppState.Success(it)) },
                        { stateLiveData.postValue(AppState.Error(it)) }
                    )
            )
        }
    }

    fun saveData(data: List<ProductShortItem>, isOnline: Boolean, isNewItem: Boolean) {
        if (isNewItem) compositeDisposable.add(
            interactor
                .getData(Query.Post.Basket.Put(ProductsUpdate(data)), isOnline)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe({
                    basketID = (it as List<BasketResponse>).first().basketId
                }, { stateLiveData.postValue(AppState.Error(it)) })
        )
        else basketID?.let { basket ->
            compositeDisposable.add(
                interactor
                    .getData(Query.Put.Basket.Update(basket, ProductsUpdate(data)), isOnline)
                    .subscribeOn(schedulerProvider.io)
                    .observeOn(schedulerProvider.io)
                    .subscribe({}, { stateLiveData.postValue(AppState.Error(it)) })
            )
        }
        if (basketID == null) stateLiveData.value =
            AppState.Error(RuntimeException("Should init BasketID first"))
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