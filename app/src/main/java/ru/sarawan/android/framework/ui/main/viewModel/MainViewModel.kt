package ru.sarawan.android.framework.ui.main.viewModel

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.framework.MainInteractor
import ru.sarawan.android.framework.ui.base.mainCatalog.BaseMainCatalogViewModel
import ru.sarawan.android.framework.ui.base.mainCatalog.CardType
import ru.sarawan.android.model.data.*
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.utils.SortBy
import ru.sarawan.android.utils.StringProvider
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider,
    private val stringProvider: StringProvider
) : BaseMainCatalogViewModel(interactor, schedulerProvider, stringProvider) {
    override fun getStartData(isOnline: Boolean, isLoggedUser: Boolean) {
        searchWord = null
        val discount = interactor.getData(Query.Get.Products.DiscountProducts(), isOnline)
        val basket =
            interactor.getData(Query.Get.Basket, isLoggedUser).onErrorReturnItem(listOf(Basket()))
        val popular = loadMoreData(isOnline, Query.Get.Products.PopularProducts(), isLoggedUser)
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

    override fun getMoreData(isOnline: Boolean, isLoggedUser: Boolean) {
        val tempWord = searchWord
        (if (tempWord == null) loadMoreData(
            isOnline,
            Query.Get.Products.PopularProducts(),
            isLoggedUser
        )
        else loadMoreData(isOnline, Query.Get.Products.ProductName(tempWord), isLoggedUser))
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