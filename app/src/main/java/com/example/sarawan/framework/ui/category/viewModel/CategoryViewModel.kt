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

    private var category: Int = -1

    fun getStartData(category: Int, isOnline: Boolean) {
        this.category = category
        getMoreData(isOnline)
    }

    override fun getStartData(isOnline: Boolean) = Unit

    override fun getMoreData(isOnline: Boolean) {
        (if (category == CategoryFragment.DISCOUNT) {
            loadMoreData(isOnline, Query.Get.Products.DiscountProducts())
        } else loadMoreData(isOnline, Query.Get.Products.ProductCategory(category)))
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io)
            .subscribe(
                { stateLiveData.postValue(AppState.Success(it)) },
                { stateLiveData.postValue(AppState.Error(it)) }
            )
    }
}