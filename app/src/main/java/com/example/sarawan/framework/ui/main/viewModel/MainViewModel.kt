package com.example.sarawan.framework.ui.main.viewModel

import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.mainCatalog.BaseMainCatalogViewModel
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
        val popular = loadMoreData(isOnline)
        compositeDisposable.add(
            Single.zip(discount, popular, basket) { discountData, popularData, basketData ->
                val data: MutableList<MainScreenDataModel> = mutableListOf()
                data.addAll(popularData)
                val basketObject = (basketData as List<Basket>).first()
                basketID = basketObject.basketId
                discountData.forEach { discountSingleData ->
                    val mainScreenData = (discountSingleData as Product).toMainScreenDataModel()
                    data.add(mainScreenData)
                    basketObject.products?.forEach { basketSingleData ->
                        if (mainScreenData.id == basketSingleData.basketProduct?.basketProduct?.id)
                            mainScreenData.quantity = basketSingleData.quantity
                    }
                }
                data.sortByDescending { it.discount }
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

    fun getMoreData(isOnline: Boolean) {
        loadMoreData(isOnline).subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io)
            .subscribe(
                { stateMoreLiveData.postValue(AppState.Success(it)) },
                { stateMoreLiveData.postValue(AppState.Error(it)) }
            )
    }

    private fun loadMoreData(
        isOnline: Boolean
    ): Single<MutableList<MainScreenDataModel>> {
        val basket = interactor.getData(Query.Get.Basket, isOnline)
        val popular = getPopularData(lastPage, isOnline)
        return Single.zip(popular, basket) { popularData, basketData ->
            val data: MutableList<MainScreenDataModel> = mutableListOf()
            val basketObject = (basketData as List<Basket>).first()
            basketID = basketObject.basketId
            popularData.forEach { popularSingleData ->
                val mainScreenData = (popularSingleData).toMainScreenDataModel()
                data.add(mainScreenData)
                basketObject.products?.forEach { basketSingleData ->
                    if (mainScreenData.id == basketSingleData.basketProduct?.basketProduct?.id)
                        mainScreenData.quantity = basketSingleData.quantity
                }
            }
            lastPage += PAGES
            data
        }
    }

    private fun getPopularData(
        pageNumber: Int,
        isOnline: Boolean
    ): Single<MutableList<Product>> {
        return if (lastPage + PAGES <= pageNumber) Single.fromCallable { mutableListOf() }
        else Single.zip(
            interactor.getData(
                Query.Get.Products.PopularProducts(pageNumber),
                isOnline
            ) as Single<MutableList<Product>>,
            getPopularData(pageNumber + 1, isOnline)
        ) { data1, data2 ->
            data1.addAll(data2)
            data1
        }
    }
}