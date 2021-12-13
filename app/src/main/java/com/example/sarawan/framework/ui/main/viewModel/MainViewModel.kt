package com.example.sarawan.framework.ui.main.viewModel

import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.mainCatalog.BaseMainCatalogViewModel
import com.example.sarawan.framework.ui.base.mainCatalog.CardType
import com.example.sarawan.model.data.*
import com.example.sarawan.rx.ISchedulerProvider
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseMainCatalogViewModel(interactor, schedulerProvider) {

    override fun getStartData(isOnline: Boolean) {
        val discount = interactor.getData(Query.Get.Products.DiscountProducts(), isOnline)
        val basket = interactor.getData(Query.Get.Basket, isOnline)
        val popular = loadMoreData(isOnline, Query.Get.Products.PopularProducts())
        compositeDisposable.add(
            Single.zip(discount, popular, basket) { discountData, popularData, basketData ->
                val data: MutableList<MainScreenDataModel> = mutableListOf()
                data.addAll(popularData)
                val basketObject = (basketData as List<Basket>).first()
                basketID = basketObject.basketId
                discountData.forEach { discountSingleData ->
                    val mainScreenData = (discountSingleData as Product)
                        .toMainScreenDataModel()
                        .apply { cardType = CardType.TOP.type }
                    data.add(mainScreenData)
                    basketObject.products?.forEach { basketSingleData ->
                        if (mainScreenData.id == basketSingleData.basketProduct?.basketProduct?.id)
                            mainScreenData.quantity = basketSingleData.quantity
                    }
                }
                data
            }
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe(
                    { stateLiveData.postValue(AppState.Success(it)) },
                    { stateLiveData.postValue(AppState.Error(it)) }
                )
        )
    }

    override fun getMoreData(isOnline: Boolean) {
        loadMoreData(isOnline, Query.Get.Products.PopularProducts())
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io)
            .subscribe(
                { stateMoreLiveData.postValue(AppState.Success(it)) },
                { stateMoreLiveData.postValue(AppState.Error(it)) }
            )
    }


}