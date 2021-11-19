package com.example.sarawan.framework.ui.main.viewModel

import android.graphics.Color
import com.example.sarawan.R
import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.BaseViewModel
import com.example.sarawan.framework.ui.main.adapter.CardType
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.DataModel
import com.example.sarawan.model.data.MainScreenDataModel
import com.example.sarawan.model.data.convertFromDataModel
import com.example.sarawan.rx.ISchedulerProvider
import io.reactivex.rxjava3.observers.DisposableObserver
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>() {

    fun search(word: String, isOnline: Boolean) {
        compositeDisposable.add(
            interactor.getData(word, isOnline)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .doOnSubscribe { stateLiveData.postValue(AppState.Loading) }
                .subscribeWith(getObserver())
        )
    }

    private fun getObserver() = object : DisposableObserver<AppState<*>>() {
        override fun onNext(appState: AppState<*>) {
            if (appState is AppState.Success) {
                val data = mutableListOf<MainScreenDataModel>()
                appState.data.forEach {
                    data.add(convertFromDataModel(it as DataModel))
                }
                getRandomPicturesAsPartners(data)
                stateLiveData.postValue(AppState.Success(data))
            }
        }

        private fun getRandomPicturesAsPartners(data: MutableList<MainScreenDataModel>) {
            for (i in 0..(Math.random() * 10 + 10).toInt()) {
                val picture = when ((Math.random() * 3).toInt()) {
                    0 -> R.drawable.test_5
                    1 -> R.drawable.test_lenta
                    else -> R.drawable.test_magnit
                }
                val color = if (picture == R.drawable.test_lenta) Color.BLUE else Color.WHITE
                data.add(
                    MainScreenDataModel(
                        backgroundColor = color,
                        padding = arrayListOf(25, 25, 0, 0),
                        pictureUrl = picture.toString(),
                        cardType = CardType.PARTNERS.type
                    )
                )
            }
        }

        override fun onError(e: Throwable) {
            stateLiveData.postValue(AppState.Error(e))
        }

        override fun onComplete() = Unit
    }
}