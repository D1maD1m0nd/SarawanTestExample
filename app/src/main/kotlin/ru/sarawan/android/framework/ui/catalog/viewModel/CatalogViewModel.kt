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
                .observeOn(schedulerProvider.ui)
                .doOnSubscribe { stateLiveData.value = AppState.Loading }
                .subscribe(
                    { categories ->
                        stateLiveData.value = AppState.Success(categories)
                    },
                    { stateLiveData.value = AppState.Error(it) }
                )
        )
    }

    override fun getMoreData(isLoggedUser: Boolean) = Unit
}