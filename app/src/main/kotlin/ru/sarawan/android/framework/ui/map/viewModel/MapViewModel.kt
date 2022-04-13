package ru.sarawan.android.framework.ui.map.viewModel

import ru.sarawan.android.framework.ui.base.BaseViewModel
import ru.sarawan.android.model.data.AppState
import ru.sarawan.android.model.interactor.MapInteractor
import ru.sarawan.android.rx.ISchedulerProvider
import javax.inject.Inject

class MapViewModel @Inject constructor(
    private val mapInteractor: MapInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>() {

    fun getCoordinated(lat: Double, lon: Double) {
        val coordinates = "$lon,$lat"
        compositeDisposable.add(
            mapInteractor.getAddressMetaData(coordinates)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.ui)
                .doOnSubscribe { stateLiveData.value = AppState.Loading }
                .subscribe({ addressItems ->
                    stateLiveData.value = AppState.Success(addressItems)
                },
                    { stateLiveData.value = AppState.Error(it) })
        )
    }
}