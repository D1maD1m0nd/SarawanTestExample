package com.example.sarawan.framework.ui.base.mainCatalog

import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.BaseViewModel
import com.example.sarawan.model.data.*
import com.example.sarawan.rx.ISchedulerProvider
import com.example.sarawan.utils.SortBy
import com.example.sarawan.utils.StringProvider
import io.reactivex.rxjava3.core.Single

abstract class BaseMainCatalogViewModel(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider,
    private val stringProvider: StringProvider
) : BaseViewModel<AppState<*>>(), MainCatalogInterface {

    protected var sortType = SortBy.PRICE_ASC

    protected var lastPage = 1

    protected var maxElement = 0

    protected var searchWord: String? = null

    protected var basketID: Int? = null

    fun search(word: String, isOnline: Boolean, errorCallback: () -> Unit) {
        sortType = SortBy.PRICE_ASC
        lastPage = 1
        searchWord = word
        compositeDisposable.add(
            loadMoreData(isOnline, Query.Get.Products.ProductName(word), errorCallback)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .doOnSubscribe { stateLiveData.postValue(AppState.Loading) }
                .subscribe(
                    { productsList ->
                        val result: MutableList<MainScreenDataModel> = mutableListOf()
                        productsList.forEach { product ->
                            sortShops(product)
                            if (isValidToShow(product))
                                result.add(
                                    product.toMainScreenDataModel(stringProvider.getString(sortType.description))
                                )
                        }
                        stateLiveData.postValue(AppState.Success(listOf(Pair(maxElement, result))))
                    },
                    { stateLiveData.postValue(AppState.Error(it)) }
                )
        )
    }

    fun saveData(data: MainScreenDataModel, isOnline: Boolean, isNewItem: Boolean) {
        val products = listOf(data.toProductShortItem())
        if (isNewItem) compositeDisposable.add(
            interactor
                .getData(Query.Post.Basket.Put(ProductsUpdate(products)), isOnline)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe({
                    basketID = (it as List<BasketResponse>).first().basketId
                }, { stateLiveData.postValue(AppState.Error(it)) })
        )
        else basketID?.let { basket ->
            compositeDisposable.add(
                interactor
                    .getData(Query.Put.Basket.Update(basket, ProductsUpdate(products)), isOnline)
                    .subscribeOn(schedulerProvider.io)
                    .observeOn(schedulerProvider.io)
                    .subscribe({}, { stateLiveData.postValue(AppState.Error(it)) })
            )
        }
        if (basketID == null) stateLiveData.value =
            AppState.Error(RuntimeException("Should init BasketID first"))
    }

    protected fun loadMoreData(
        isOnline: Boolean,
        query: Query.Get.Products,
        errorCallback: () -> Unit
    ): Single<MutableList<Product>> {
        val basket =
            interactor.getData(Query.Get.Basket, isOnline).onErrorReturnItem(listOf(Basket()))
        val productsData = getProductsData(isOnline, query.apply { page = lastPage }, errorCallback)
        return Single.zip(productsData, basket) { responseData, basketData ->
            val data: MutableList<Product> = mutableListOf()
            val basketObject = (basketData as List<Basket>).firstOrNull()
            basketID = basketObject?.basketId
            responseData.forEach {
                maxElement = it.count
                it.results.forEach { singleData ->
                    data.add(singleData)
                    getQuantityFromBasket(basketObject, singleData)
                }
            }
            lastPage += PAGES
            data
        }
    }

    protected fun getQuantityFromBasket(
        basketObject: Basket?,
        singleData: Product
    ) {
        basketObject?.products?.forEach { basketSingleData ->
            if (singleData.id == basketSingleData.basketProduct?.basketProduct?.id)
                singleData.storePrices?.forEach { store ->
                    if (store.id == basketSingleData.basketProduct?.productStoreId)
                        store.count = basketSingleData.quantity ?: 0
                }
        }
    }

    private fun getProductsData(
        isOnline: Boolean,
        query: Query.Get.Products,
        errorCallback: () -> Unit
    ): Single<MutableList<Response>> {
        return if (lastPage + PAGES <= query.page) Single.fromCallable { mutableListOf() }
        else {
            val singleData =
                (interactor.getData(query, isOnline) as Single<MutableList<Response>>)
                    .onErrorReturn {
                        errorCallback()
                        mutableListOf()
                    }
            Single.zip(
                singleData,
                getProductsData(isOnline, query.apply { page += 1 }, errorCallback)
            ) { data1, data2 ->
                data1.addAll(data2)
                data1
            }
        }
    }

    abstract fun getMoreData(isOnline: Boolean, sortType: SortBy, errorCallback: () -> Unit)

    fun clear() {
        stateLiveData.value = AppState.Empty
    }

    protected fun sortShops(product: Product) {
        product.apply {
            when (sortType) {
                SortBy.PRICE_ASC -> storePrices?.sortBy { it.price }
                SortBy.PRICE_DES -> storePrices?.sortByDescending { it.price }
                SortBy.ALPHABET -> Unit
                SortBy.DISCOUNT -> storePrices?.sortByDescending { it.discount }
            }
        }
    }

    protected fun isValidToShow(product: Product) =
        product.storePrices?.findLast { it.count > 0 } == product.storePrices?.first() ||
                product.storePrices?.sumOf { it.count } == 0

    companion object {
        const val PAGES = 3
    }
}