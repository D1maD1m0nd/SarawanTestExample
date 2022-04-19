package ru.sarawan.android.framework.ui.base.mainCatalog

import io.reactivex.rxjava3.core.Single
import retrofit2.HttpException
import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.*
import ru.sarawan.android.model.data.product.*
import ru.sarawan.android.model.interactor.BasketInteractor
import ru.sarawan.android.model.interactor.ProductInteractor
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.utils.StringProvider
import ru.sarawan.android.utils.constants.SortBy

abstract class BaseMainCatalogViewModel(
    private val productsInteractor: ProductInteractor,
    private val basketInteractor: BasketInteractor,
    private val schedulerProvider: ISchedulerProvider,
    private val stringProvider: StringProvider
) : BaseViewModel<AppState<*>>(), MainCatalogInterface {

    protected var sortType = SortBy.PRICE_ASC
    protected var lastPage = 1
    protected var isLastPage = false
    protected var searchWord: String? = null
    protected var category: Int? = null
    protected var subcategory: Int? = null
    protected var filters: List<Filter>? = null
    protected var basketID: Int? = null
    protected var prevCategory: Int? = null

    fun search(
        word: String?,
        categoryFilter: Int?,
        subcategory: Int?,
        isLoggedUser: Boolean,
        searchType: SortBy = sortType
    ) {
        onCleared()
        sortType = searchType
        lastPage = 1
        if (categoryFilter != null && word.isNullOrEmpty()) prevCategory = categoryFilter
        searchWord = word
        category = if (subcategory != null) null else (categoryFilter ?: prevCategory)
//        val foundSubcategory = filters?.find { it.id == subcategory?.toLong() }?.id?.toInt()
        compositeDisposable.add(
            loadMoreData(
                Products(
                    productName = word,
                    pageSize = PAGE_ELEMENTS,
                    categoryFilter = if (subcategory == null) categoryFilter
                        ?: prevCategory else null,
                    subcategory = subcategory,
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
                            AppState.Success(MainScreenDataModel(result, isLastPage, filters))
                        )
                    },
                    { stateLiveData.postValue(AppState.Error(it)) }
                )
        )
    }

    fun saveData(data: CardScreenDataModel, isLoggedUser: Boolean, isNewItem: Boolean) {
        onCleared()
        val products = listOf(data.toProduct())
        if (isNewItem) compositeDisposable.add(
            basketInteractor
                .putProduct(isLoggedUser, ProductsUpdate(products))
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe(
                    { basketID = it.id },
                    { stateLiveData.postValue(AppState.Error(it)) }
                )
        )
        else basketID?.let { basket ->
            compositeDisposable.add(
                basketInteractor
                    .updateProduct(isLoggedUser, basket, ProductsUpdate(products))
                    .subscribeOn(schedulerProvider.io)
                    .observeOn(schedulerProvider.io)
                    .subscribe({}, { stateLiveData.postValue(AppState.Error(it)) })
            )
        }
        if (basketID == null) stateLiveData.value =
            AppState.Error(RuntimeException("Should init BasketID first"))
    }

    protected fun loadMoreData(
        products: Products,
        isLoggedUser: Boolean
    ): Single<MutableList<Product>> {
        val basket = basketInteractor.getBasket(isLoggedUser).onErrorReturnItem(Basket())
        val productsData = getProductsData(products.apply {
            page = lastPage
            lastPage++
        })
        return Single.zip(productsData, basket) { response, basketData ->
            basketID = basketData.basketId
            filters = response.filters
            isLastPage = response.nextPage.isNullOrEmpty()
            val data: MutableList<Product> = response.results.toMutableList()
            data.forEach { getQuantityFromBasket(basketData, it) }
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

    private fun getProductsData(products: Products): Single<Response> =
        productsInteractor.getProducts(products)
            .onErrorReturn {
                if (it is HttpException && it.code() == 404) Response(emptyList(), null, null)
                else throw it
            }

    abstract fun getMoreData(isLoggedUser: Boolean)

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
        const val PAGE_ELEMENTS = 50
    }
}