package ru.sarawan.android.framework.ui.map.viewModel

import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.interactor.BasketInteractor
import ru.sarawan.android.model.interactor.ProductInteractor
import ru.sarawan.android.rx.ISchedulerProvider
import javax.inject.Inject

class MapViewModel @Inject constructor(
    private val productInteractor: ProductInteractor,
    private val basketInteractor: BasketInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>() {

    fun getCoordinated() {

    }
}