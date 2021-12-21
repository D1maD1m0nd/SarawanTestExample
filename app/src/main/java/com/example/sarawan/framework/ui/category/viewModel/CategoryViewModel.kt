package com.example.sarawan.framework.ui.category.viewModel

import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.mainCatalog.BaseMainCatalogViewModel
import com.example.sarawan.framework.ui.category.CategoryFragment
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.MainScreenDataModel
import com.example.sarawan.model.data.Query
import com.example.sarawan.model.data.toMainScreenDataModel
import com.example.sarawan.rx.ISchedulerProvider
import com.example.sarawan.utils.SortBy
import com.example.sarawan.utils.StringProvider
import javax.inject.Inject

class CategoryViewModel @Inject constructor(
    interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider,
    private val stringProvider: StringProvider
) : BaseMainCatalogViewModel(interactor, schedulerProvider, stringProvider) {

    private var category: Int = CategoryFragment.DISCOUNT

    override fun getStartData(isOnline: Boolean, errorCallback: () -> Unit) = Unit

    override fun getMoreData(isOnline: Boolean, sortType: SortBy, errorCallback: () -> Unit) {
        (if (category == CategoryFragment.DISCOUNT) {
            loadMoreData(isOnline, Query.Get.Products.DiscountProducts(sortBy = sortType), errorCallback)
        } else loadMoreData(isOnline, Query.Get.Products.ProductCategory(category, sortBy = sortType), errorCallback))
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

    fun changeSorting(category: Int, isOnline: Boolean, sortType: SortBy, errorCallback: () -> Unit){
        stateLiveData.value = AppState.Loading
        lastPage = 1
        this.category = category
        this.sortType = sortType
        getMoreData(isOnline, sortType, errorCallback)
    }
}