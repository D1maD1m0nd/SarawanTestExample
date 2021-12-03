package com.example.sarawan.framework.ui.main.viewModel

import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.mainCatalog.BaseMainCatalogViewModel
import com.example.sarawan.model.data.*
import com.example.sarawan.rx.ISchedulerProvider
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseMainCatalogViewModel(interactor, schedulerProvider) {

    override fun getStartData(isOnline: Boolean) {
        val discount = interactor.getData(Query.Get.Products.DiscountProducts(), isOnline)
        val popular = interactor.getData(Query.Get.Products.PopularProducts(), isOnline)
        val basket = interactor.getData(Query.Get.Basket, isOnline)
        compositeDisposable.add(
            discount.zipWith(popular) { discountData, popularData ->
                val data: MutableList<MainScreenDataModel> = mutableListOf()
                discountData.forEach {
                    data.add((it as Product).toMainScreenDataModel())
                }
                popularData.forEach {
                    data.add((it as Product).toMainScreenDataModel())
                }
//                getRandomPicturesAsPartners(data)
                data.sortedByDescending { it.discount }
            }.zipWith(basket) { data, basketData ->
                val basketObject = (basketData as List<Basket>).first()
                basketID = basketObject.basketId
                data.forEach { mainScreenData ->
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
}