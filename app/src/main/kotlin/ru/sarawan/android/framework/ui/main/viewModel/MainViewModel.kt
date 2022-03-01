package ru.sarawan.android.framework.ui.main.viewModel

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.framework.ui.base.mainCatalog.BaseMainCatalogViewModel
import ru.sarawan.android.framework.ui.base.mainCatalog.CardType
import ru.sarawan.android.model.data.*
import ru.sarawan.android.model.interactor.BasketInteractor
import ru.sarawan.android.model.interactor.ProductInteractor
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.utils.StringProvider
import ru.sarawan.android.utils.constants.SortBy
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val productInteractor: ProductInteractor,
    private val basketInteractor: BasketInteractor,
    private val schedulerProvider: ISchedulerProvider,
    private val stringProvider: StringProvider
) : BaseMainCatalogViewModel(
    productInteractor,
    basketInteractor,
    schedulerProvider,
    stringProvider
) {
    override fun getStartData(isOnline: Boolean, isLoggedUser: Boolean) {
        lastPage = 1
        searchWord = null
        category = null
        val discount = productInteractor.getProducts(Products(discountProduct = true))
        val basket = basketInteractor.getBasket(isLoggedUser).onErrorReturnItem(Basket())
        val popular =
            loadMoreData(Products(popularProducts = true, pageSize = PAGE_ELEMENTS), isLoggedUser)
        compositeDisposable.add(
            Single.zip(discount, popular, basket) { discountData, popularData, basketData ->
                val data: MutableList<CardScreenDataModel> = mutableListOf()
                popularData.forEach { product ->
                    sortShops(product)
                    if (isValidToShow(product))
                        data.add(product.toMainScreenDataModel(stringProvider.getString(sortType.description)))
                }
                basketID = basketData.basketId
                discountData.results.forEach { discountSingleData ->
                    discountSingleData.apply { storePrices?.sortByDescending { it.discount } }
                    getQuantityFromBasket(basketData, discountSingleData)
                    if (isValidToShow(discountSingleData))
                        data.add(discountSingleData
                            .toMainScreenDataModel(stringProvider.getString(SortBy.DISCOUNT.description))
                            .apply { cardType = CardType.TOP.type })
                }
                data
            }
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe(
                    {
                        stateLiveData.postValue(
                            AppState.Success(listOf(MainScreenDataModel(it, isLastPage, filters)))
                        )
                    },
                    { stateLiveData.postValue(AppState.Error(it)) }
                )
        )
    }

    override fun getMoreData(isLoggedUser: Boolean) {
        val products: Products = if (searchWord == null) Products(
            popularProducts = true,
            pageSize = PAGE_ELEMENTS,
            categoryFilter = category
        ) else Products(
            productName = searchWord,
            pageSize = PAGE_ELEMENTS,
            categoryFilter = category,
            sortBy = sortType
        )

        loadMoreData(products, isLoggedUser)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io)
            .subscribe(
                { productsList ->
                    val result: MutableList<CardScreenDataModel> = mutableListOf()
                    productsList.forEach { product ->
                        sortShops(product)
                        if (isValidToShow(product))
                            result.add(
                                product.toMainScreenDataModel(stringProvider.getString(sortType.description))
                            )
                    }
                    stateLiveData.postValue(
                        AppState.Success(listOf(MainScreenDataModel(result, isLastPage, filters)))
                    )
                },
                { stateLiveData.postValue(AppState.Error(it)) }
            )
    }


}