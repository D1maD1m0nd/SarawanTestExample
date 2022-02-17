package ru.sarawan.android.framework.ui.base.mainCatalog

import io.reactivex.rxjava3.core.Single
import retrofit2.HttpException
import ru.sarawan.android.framework.MainInteractor
import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.*
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.utils.StringProvider
import ru.sarawan.android.utils.constants.SortBy

abstract class BaseMainCatalogViewModel(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider,
    private val stringProvider: StringProvider
) : BaseViewModel<AppState<*>>(), MainCatalogInterface {

    protected var sortType = SortBy.PRICE_ASC

    protected var lastPage = 1

    protected var maxElement = 0

    protected var searchWord: String? = null
    protected var category: Int? = null

    protected var filters: List<Filter>? = null

    protected var basketID: Int? = null

    fun search(
        word: String?,
        categoryFilter: Int?,
        isOnline: Boolean,
        isLoggedUser: Boolean,
        searchType: SortBy = SortBy.PRICE_ASC
    ) {
        sortType = searchType
        lastPage = 1
        searchWord = word
        category = categoryFilter
        compositeDisposable.add(
            loadMoreData(
                isOnline,
                Query.Get.Products(
                    productName = word,
                    categoryFilter = categoryFilter,
                    sortBy = searchType
                ),
                isLoggedUser
            )
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .doOnSubscribe { stateLiveData.postValue(AppState.Loading) }
                .subscribe(
                    { productsList ->
                        val result: MutableList<CardScreenDataModel> = mutableListOf()
                        productsList.forEach { product ->
                            sortShops(product)
                            if (isValidToShow(product)) result
                                .add(product.toMainScreenDataModel(stringProvider.getString(sortType.description)))
                        }
                        stateLiveData.postValue(
                            AppState.Success(
                                listOf(MainScreenDataModel(result, maxElement, filters))
                            )
                        )
                    },
                    { stateLiveData.postValue(AppState.Error(it)) }
                )
        )
    }

    fun saveData(data: CardScreenDataModel, isLoggedUser: Boolean, isNewItem: Boolean) {
        val products = listOf(data.toProduct())
        if (isNewItem) compositeDisposable.add(
            interactor
                .getData(Query.Post.Basket.Put(ProductsUpdate(products)), isLoggedUser)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe({
                    basketID = (it as List<BasketResponse>).first().basketId
                }, { stateLiveData.postValue(AppState.Error(it)) })
        )
        else basketID?.let { basket ->
            compositeDisposable.add(
                interactor
                    .getData(
                        Query.Put.Basket.Update(basket, ProductsUpdate(products)),
                        isLoggedUser
                    )
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
        isLoggedUser: Boolean
    ): Single<MutableList<Product>> {
        val basket =
            interactor.getData(Query.Get.Basket, isLoggedUser).onErrorReturnItem(listOf(Basket()))
        val productsData = getProductsData(isOnline, query.apply { page = lastPage })
        return Single.zip(productsData, basket) { responseData, basketData ->
            val data: MutableList<Product> = mutableListOf()
            val basketObject = (basketData as List<Basket>).firstOrNull()
            basketID = basketObject?.basketId
            responseData.forEach { response ->
                filters = response.filters
                maxElement = when {
                    category != null -> {
                        response.filters?.find { it.id == category }?.let { return@let it }
                        response.count
                    }
                    else -> response.count
                }
                response.results.forEach { singleData ->
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
        query: Query.Get.Products
    ): Single<MutableList<Response>> {
        return if (lastPage + PAGES <= query.page) Single.fromCallable { mutableListOf() }
        else {
            val singleData = (interactor.getData(query, isOnline) as Single<MutableList<Response>>)
                .onErrorReturn {
                    if (it is HttpException && it.code() == 404) mutableListOf()
                    else throw it
                }
            Single.zip(singleData, getProductsData(isOnline, query.apply { page += 1 }))
            { data1, data2 ->
                data1.addAll(data2)
                data1
            }
        }
    }

    abstract fun getMoreData(isOnline: Boolean, isLoggedUser: Boolean)

    protected fun sortShops(product: Product) {
        product.apply {
            when (sortType) {
                SortBy.PRICE_ASC -> storePrices?.sortBy { it.price }
                SortBy.PRICE_DES -> storePrices?.sortBy { it.price }
                SortBy.ALPHABET -> Unit
                SortBy.DISCOUNT -> storePrices?.sortByDescending { it.discount }
            }
        }
    }

    protected fun isValidToShow(product: Product): Boolean {
        if (product.storePrices.isNullOrEmpty()) return false
        return product.storePrices.findLast { it.count > 0 } == product.storePrices.first() ||
                product.storePrices.sumOf { it.count } == 0
    }

    companion object {
        const val PAGES = 3
    }
}