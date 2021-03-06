package ru.sarawan.android.framework.ui.main.viewModel

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.framework.ui.base.mainCatalog.BaseMainCatalogViewModel
import ru.sarawan.android.framework.ui.base.mainCatalog.CardType
import ru.sarawan.android.model.data.*
import ru.sarawan.android.model.data.basket.Basket
import ru.sarawan.android.model.data.product.Products
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
        val discount = productInteractor.getProducts(
            Products(discountProduct = true, sortBy = SortBy.DISCOUNT)
        )
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
                .subscribeOn(schedulerProvider.computation)
                .observeOn(schedulerProvider.ui)
                .subscribe(
                    {
                        stateLiveData.value =
                            AppState.Success(MainScreenDataModel(it, isLastPage, filters))

                    },
                    { stateLiveData.value = AppState.Error(it) }
                )
        )
    }

    override fun getMoreData(isLoggedUser: Boolean) {
        onCleared()
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
        compositeDisposable.add(
            loadMoreData(products, isLoggedUser)
                .map { productsList ->
                    val result: MutableList<CardScreenDataModel> = mutableListOf()
                    productsList.forEach { product ->
                        sortShops(product)
                        if (isValidToShow(product))
                            result.add(
                                product.toMainScreenDataModel(stringProvider.getString(sortType.description))
                            )
                    }
                    result
                }
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .subscribe(
                    {
                        stateLiveData.value =
                            AppState.Success(MainScreenDataModel(it, isLastPage, filters))

                    },
                    { stateLiveData.value = AppState.Error(it) }
                )
        )
    }


}