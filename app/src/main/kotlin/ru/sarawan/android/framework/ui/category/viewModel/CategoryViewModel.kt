package ru.sarawan.android.framework.ui.category.viewModel

import ru.sarawan.android.framework.ui.base.mainCatalog.BaseMainCatalogViewModel
import ru.sarawan.android.framework.ui.category.CategoryFragment
import ru.sarawan.android.model.data.*
import ru.sarawan.android.model.interactor.BasketInteractor
import ru.sarawan.android.model.interactor.ProductInteractor
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.utils.StringProvider
import ru.sarawan.android.utils.constants.SortBy
import javax.inject.Inject

class CategoryViewModel @Inject constructor(
    productInteractor: ProductInteractor,
    basketInteractor: BasketInteractor,
    private val schedulerProvider: ISchedulerProvider,
    private val stringProvider: StringProvider
) : BaseMainCatalogViewModel(
    productInteractor,
    basketInteractor,
    schedulerProvider,
    stringProvider
) {

    override fun getStartData(isOnline: Boolean, isLoggedUser: Boolean) {
        category = CategoryFragment.DISCOUNT
    }

    override fun getMoreData(isLoggedUser: Boolean) {
        onCleared()
        val products: Products =
            if (category == CategoryFragment.DISCOUNT) Products(
                sortBy = sortType,
                pageSize = PAGE_ELEMENTS,
                discountProduct = true
            ) else Products(
                categoryFilter = category,
                pageSize = PAGE_ELEMENTS,
                subcategory = subcategory,
                sortBy = sortType
            )
        compositeDisposable.add(
            loadMoreData(products, isLoggedUser)
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
                            AppState.Success(MainScreenDataModel(result, isLastPage, filters))
                        )
                    },
                    { stateLiveData.postValue(AppState.Error(it)) }
                )
        )
    }

    fun changeSorting(
        category: Int?,
        subcategory: Int?,
        sortType: SortBy,
        isLoggedUser: Boolean
    ) {
        stateLiveData.value = AppState.Loading
        lastPage = 1
        prevCategory = category
        this.category = if (subcategory != null) null else category
        this.sortType = sortType
        this.subcategory = subcategory
        getMoreData(isLoggedUser)
    }
}