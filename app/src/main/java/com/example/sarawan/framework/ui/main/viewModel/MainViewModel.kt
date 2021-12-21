package com.example.sarawan.framework.ui.main.viewModel

import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.mainCatalog.BaseMainCatalogViewModel
import com.example.sarawan.framework.ui.base.mainCatalog.CardType
import com.example.sarawan.model.data.*
import com.example.sarawan.rx.ISchedulerProvider
import com.example.sarawan.utils.SortBy
import com.example.sarawan.utils.StringProvider
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider,
    private val stringProvider: StringProvider
) : BaseMainCatalogViewModel(interactor, schedulerProvider, stringProvider) {

    override fun getStartData(isOnline: Boolean, errorCallback: () -> Unit) {
        searchWord = null
        val discount = interactor.getData(Query.Get.Products.DiscountProducts(), isOnline)
        val basket =
            interactor.getData(Query.Get.Basket, isOnline).onErrorReturnItem(listOf(Basket()))
        val popular = loadMoreData(isOnline, Query.Get.Products.PopularProducts(), errorCallback)
        compositeDisposable.add(
            Single.zip(discount, popular, basket) { discountData, popularData, basketData ->
                val data: MutableList<MainScreenDataModel> = mutableListOf()
                popularData.forEach { product ->
                    sortShops(product)
                    if (isValidToShow(product))
                        data.add(product.toMainScreenDataModel(stringProvider.getString(sortType.description)))
                }
                val basketObject = (basketData as List<Basket>).firstOrNull()
                basketID = basketObject?.basketId
                (discountData.first() as Response).results.forEach { discountSingleData ->
                    discountSingleData.apply { storePrices?.sortByDescending { it.discount } }
                    getQuantityFromBasket(basketObject, discountSingleData)
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
                    { stateLiveData.postValue(AppState.Success(listOf(Pair(maxElement, it)))) },
                    { stateLiveData.postValue(AppState.Error(it)) }
                )
        )
    }

    override fun getMoreData(isOnline: Boolean, sortType: SortBy, errorCallback: () -> Unit) {
        this.sortType = sortType
        val tempWord = searchWord
        (if (tempWord == null) loadMoreData(
            isOnline,
            Query.Get.Products.PopularProducts(),
            errorCallback
        )
        else loadMoreData(isOnline, Query.Get.Products.ProductName(tempWord), errorCallback))
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io)
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
    }


}