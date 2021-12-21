package com.example.sarawan.framework.ui.catalog.viewModel

import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.mainCatalog.BaseMainCatalogViewModel
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.CategoryDataModel
import com.example.sarawan.model.data.Query
import com.example.sarawan.rx.ISchedulerProvider
import com.example.sarawan.utils.SortBy
import com.example.sarawan.utils.StringProvider
import javax.inject.Inject

class CatalogViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider,
    stringProvider: StringProvider
) : BaseMainCatalogViewModel(interactor, schedulerProvider, stringProvider) {

    override fun getStartData(isOnline: Boolean, errorCallback: () -> Unit) {
        compositeDisposable.add(
            interactor.getData(Query.Get.Category, isOnline)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .doOnSubscribe { stateLiveData.postValue(AppState.Loading) }
                .subscribe(
                    { stateLiveData.postValue(AppState.Success(it as List<CategoryDataModel>)) },
                    { stateLiveData.postValue(AppState.Error(it)) }
                )
        )
    }

    override fun getMoreData(isOnline: Boolean, sortType: SortBy, errorCallback: () -> Unit) = Unit
}