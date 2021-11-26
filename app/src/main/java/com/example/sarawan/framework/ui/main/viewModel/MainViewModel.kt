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

    private var basketID: Int? = null

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
        val discount = interactor.getData(Query.Get.Products.DiscountProducts, isOnline)
        val popular = interactor.getData(Query.Get.Products.PopularProducts, isOnline)
        val basket = interactor.getData(Query.Get.Basket, isOnline)
        compositeDisposable.add(
            discount.zipWith(popular) { discountData, popularData ->
                val data: MutableList<MainScreenDataModel> = mutableListOf()
                discountData.forEach {
                    data.add(convertFromProduct(it as Product))
                }
                popularData.forEach {
                    data.add(convertFromProduct(it as Product))
                }
                getRandomPicturesAsPartners(data)
                data.sortedByDescending { it.discount }
            }.zipWith(basket) { data, basketData ->
                val basketObject = (basketData as List<Basket>)[0]
                basketID = basketObject.basketId
                data.forEach { mainScreenData ->
                    basketObject.products?.forEach { basketSingleData ->
                        if (mainScreenData.id == basketSingleData.id)
                            mainScreenData.quantity = basketSingleData.quantity
                    }
                }
                data
            }
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe({
                    stateLiveData.postValue(AppState.Success(it))
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