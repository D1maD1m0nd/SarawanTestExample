package com.example.sarawan.framework.ui.category.viewModel

import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.mainCatalog.BaseMainCatalogViewModel
import com.example.sarawan.model.data.*
import com.example.sarawan.rx.ISchedulerProvider
import javax.inject.Inject

class CategoryViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseMainCatalogViewModel(interactor, schedulerProvider) {

    override fun getStartData(isOnline: Boolean) {
        val discount = interactor.getData(Query.Get.Products.DiscountProducts(), isOnline)
        val basket = interactor.getData(Query.Get.Basket, isOnline)
        compositeDisposable.add(
            discount.zipWith(basket) { discountData, basketData ->
                val basketObject = (basketData as List<Basket>).first()
                basketID = basketObject.basketId
                val data: MutableList<MainScreenDataModel> = mutableListOf()
                discountData.forEach {
                    val mainScreenData = (it as Product).toMainScreenDataModel()
                    basketObject.products?.forEach { basketSingleData ->
                        if (mainScreenData.id == basketSingleData.basketProduct?.basketProduct?.id)
                            mainScreenData.quantity = basketSingleData.quantity
                    }
                    data.add(mainScreenData)
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

    fun getCategoryData(category: Int, isOnline: Boolean) {
        val categories = interactor.getData(Query.Get.Products.ProductCategory(category), isOnline)
        val basket = interactor.getData(Query.Get.Basket, isOnline)
        compositeDisposable.add(
            categories.zipWith(basket) { categoryData, basketData ->
                val basketObject = (basketData as List<Basket>).first()
                basketID = basketObject.basketId
                val data: MutableList<MainScreenDataModel> = mutableListOf()
                categoryData.forEach {
                    val mainScreenData = (it as Product).toMainScreenDataModel()
                    basketObject.products?.forEach { basketSingleData ->
                        if (mainScreenData.id == basketSingleData.basketProduct?.basketProduct?.id)
                            mainScreenData.quantity = basketSingleData.quantity
                    }
                    data.add(mainScreenData)
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