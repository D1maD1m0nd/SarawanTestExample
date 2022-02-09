package ru.sarawan.android.framework.ui.category.viewModel

import ru.sarawan.android.framework.MainInteractor
import ru.sarawan.android.framework.ui.base.mainCatalog.BaseMainCatalogViewModel
import ru.sarawan.android.framework.ui.category.CategoryFragment
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.data.MainScreenDataModel
import ru.sarawan.android.model.data.Query
import ru.sarawan.android.model.data.toMainScreenDataModel
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.utils.StringProvider
import ru.sarawan.android.utils.constants.SortBy
import javax.inject.Inject

class CategoryViewModel @Inject constructor(
    interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider,
    private val stringProvider: StringProvider
) : BaseMainCatalogViewModel(interactor, schedulerProvider, stringProvider) {

    private var category: Int = CategoryFragment.DISCOUNT

    override fun getStartData(isOnline: Boolean, isLoggedUser: Boolean) = Unit

    override fun getMoreData(isOnline: Boolean, isLoggedUser: Boolean) {
        (if (category == CategoryFragment.DISCOUNT) {
            loadMoreData(
                isOnline,
                Query.Get.Products.DiscountProducts(sortBy = this.sortType),
                isLoggedUser
            )
        } else loadMoreData(
            isOnline,
            Query.Get.Products.ProductCategory(category, sortBy = this.sortType),
            isLoggedUser
        ))
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

    fun changeSorting(category: Int, isOnline: Boolean, sortType: SortBy, isLoggedUser: Boolean) {
        stateLiveData.value = AppState.Loading
        lastPage = 1
        this.category = category
        this.sortType = sortType
        getMoreData(isOnline, isLoggedUser)
    }
}