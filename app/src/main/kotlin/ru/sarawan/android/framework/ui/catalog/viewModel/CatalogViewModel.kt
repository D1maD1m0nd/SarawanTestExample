package ru.sarawan.android.framework.ui.catalog.viewModel

import ru.sarawan.android.framework.ui.base.mainCatalog.BaseMainCatalogViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.interactor.BasketInteractor
import ru.sarawan.android.model.interactor.ProductInteractor
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.utils.StringProvider
import javax.inject.Inject

class CatalogViewModel @Inject constructor(
    private val productInteractor: ProductInteractor,
    basketInteractor: BasketInteractor,
    private val schedulerProvider: ISchedulerProvider,
    stringProvider: StringProvider
) : BaseMainCatalogViewModel(
    productInteractor,
    basketInteractor,
    schedulerProvider,
    stringProvider
) {

    override fun getStartData(isOnline: Boolean, isLoggedUser: Boolean) {
        compositeDisposable.add(
            productInteractor.getCategories()
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .doOnSubscribe { stateLiveData.postValue(AppState.Loading) }
                .subscribe(
                    { categories ->
//                        filters = categories.flatMap { it.categories }.map { it.toFilter() }
                        stateLiveData.postValue(AppState.Success(categories))
                    },
                    { stateLiveData.postValue(AppState.Error(it)) }
                )
        )
    }

    override fun getMoreData(isLoggedUser: Boolean) = Unit
}