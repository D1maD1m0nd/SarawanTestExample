package com.example.sarawan.framework.ui.category.viewModel

import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.mainCatalog.BaseMainCatalogViewModel
import com.example.sarawan.framework.ui.category.CategoryFragment
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.Query
import com.example.sarawan.rx.ISchedulerProvider
import javax.inject.Inject

class CategoryViewModel @Inject constructor(
    interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseMainCatalogViewModel(interactor, schedulerProvider) {

    private var category: Int = CategoryFragment.DISCOUNT

    fun getStartData(category: Int, isOnline: Boolean, errorCallback: () -> Unit) {
        lastPage = 1
        this.category = category
        getMoreData(isOnline, errorCallback)
    }

    override fun getStartData(isOnline: Boolean, errorCallback: () -> Unit) = Unit

    override fun getMoreData(isOnline: Boolean, errorCallback: () -> Unit) {
        (if (category == CategoryFragment.DISCOUNT) {
            loadMoreData(isOnline, Query.Get.Products.DiscountProducts(), errorCallback)
        } else loadMoreData(isOnline, Query.Get.Products.ProductCategory(category), errorCallback))
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io)
            .subscribe(
                { stateLiveData.postValue(AppState.Success(listOf(Pair(maxElement ,it)))) },
                { stateLiveData.postValue(AppState.Error(it)) }
            )
    }
}