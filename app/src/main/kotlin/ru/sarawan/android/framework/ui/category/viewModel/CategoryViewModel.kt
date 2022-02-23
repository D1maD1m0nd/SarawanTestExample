package ru.sarawan.android.framework.ui.category.viewModel

import ru.sarawan.android.framework.MainInteractor
import ru.sarawan.android.framework.ui.base.mainCatalog.BaseMainCatalogViewModel
import ru.sarawan.android.framework.ui.category.CategoryFragment
import ru.sarawan.android.model.data.*
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.utils.StringProvider
import ru.sarawan.android.utils.constants.SortBy
import javax.inject.Inject

class CategoryViewModel @Inject constructor(
    interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider,
    private val stringProvider: StringProvider
) : BaseMainCatalogViewModel(interactor, schedulerProvider, stringProvider) {

    override fun getStartData(isOnline: Boolean, isLoggedUser: Boolean) {
        category = CategoryFragment.DISCOUNT
    }

    override fun getMoreData(isOnline: Boolean, isLoggedUser: Boolean) {
        val query: Query.Get.Products =
            if (category == CategoryFragment.DISCOUNT) Query.Get.Products(
                sortBy = sortType,
                discountProduct = true
            ) else Query.Get.Products(
                categoryFilter = category,
                subcategory = subcategory,
                sortBy = sortType
            )

        loadMoreData(isOnline, query, isLoggedUser)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.io)
            .subscribe(
                { productsList ->
                    val result: MutableList<CardScreenDataModel> = mutableListOf()
                    productsList.forEach { product ->
                        sortShops(product)
                        if (isValidToShow(product))
                            result.add(
                                product.toMainScreenDataModel(stringProvider.getString(sortType.description))
                            )
                    }
                    stateLiveData.postValue(
                        AppState.Success(listOf(MainScreenDataModel(result, maxElement, filters)))
                    )
                },
                { stateLiveData.postValue(AppState.Error(it)) }
            )
    }

    fun changeSorting(
        category: Int?,
        subcategory: Int?,
        isOnline: Boolean,
        sortType: SortBy,
        isLoggedUser: Boolean
    ) {
        stateLiveData.value = AppState.Loading
        lastPage = 1
        this.category = if (subcategory != null) null else category
        this.sortType = sortType
        this.subcategory = subcategory
        getMoreData(isOnline, isLoggedUser)
    }
}