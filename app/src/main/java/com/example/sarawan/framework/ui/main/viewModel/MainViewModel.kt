package com.example.sarawan.framework.ui.main.viewModel

import android.graphics.Color
import com.example.sarawan.R
import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.BaseViewModel
import com.example.sarawan.framework.ui.main.adapter.CardType
import com.example.sarawan.model.data.*
import com.example.sarawan.rx.ISchedulerProvider
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>() {

    fun search(word: String, isOnline: Boolean) {
        compositeDisposable.add(
            interactor.getData(Query.Get.Products.ProductName(word), isOnline)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .doOnSubscribe { stateLiveData.postValue(AppState.Loading) }
                .subscribe({ dataList ->
                    val data: MutableList<MainScreenDataModel> = mutableListOf()
                    dataList.forEach {
                        data.add(convertFromProduct(it as Product))
                    }
                    getRandomPicturesAsPartners(data)
                    stateLiveData.postValue(AppState.Success(data))
                }, {
                    stateLiveData.postValue(AppState.Error(it))
                })
        )
    }

    fun getStartData(isOnline: Boolean) {
        compositeDisposable.add(
            interactor.getData(Query.Get.Products.DiscountProducts, isOnline)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe({ dataList ->
                    val data: MutableList<MainScreenDataModel> = mutableListOf()
                    dataList.forEach {
                        data.add(convertFromProduct(it as Product))
                    }
                    getRandomPicturesAsPartners(data)
                    stateLiveData.postValue(AppState.Success(data))
                }, {
                    stateLiveData.postValue(AppState.Error(it))
                })
        )
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
}