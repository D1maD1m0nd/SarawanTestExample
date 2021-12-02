package com.example.sarawan.framework.ui.base.mainCatalog

import android.graphics.Color
import com.example.sarawan.R
import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.BaseViewModel
import com.example.sarawan.model.data.*
import com.example.sarawan.rx.ISchedulerProvider

abstract class BaseMainCatalogViewModel(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>(), MainCatalogInterface {

    protected var basketID: Int? = null

    override fun search(word: String, isOnline: Boolean) {
        val search = interactor.getData(Query.Get.Products.ProductName(word), isOnline)
        val basket = interactor.getData(Query.Get.Basket, isOnline)
        compositeDisposable.add(
            search.zipWith(basket) { searchData, basketData ->
                val data: MutableList<MainScreenDataModel> = mutableListOf()
                val basketObject = (basketData as List<Basket>).first()
                basketID = basketObject.basketId
                searchData.forEach {
                    val product = (it as Product).toMainScreenDataModel()
                    data.add(product)
                    basketObject.products?.forEach { basketSingleData ->
                        if (product.id == basketSingleData.basketProduct?.basketProduct?.id)
                            product.quantity = basketSingleData.quantity
                    }
                }
//                getRandomPicturesAsPartners(data)
                data.sortedByDescending { it.discount }
            }
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .doOnSubscribe { stateLiveData.postValue(AppState.Loading) }
                .subscribe(
                    { stateLiveData.postValue(AppState.Success(it)) },
                    { stateLiveData.postValue(AppState.Error(it)) }
                )
        )
    }

    override fun saveData(data: MainScreenDataModel, isOnline: Boolean, isNewItem: Boolean) {
        val products = listOf(data.toProductShortItem())
        if (isNewItem) compositeDisposable.add(
            interactor
                .getData(Query.Post.Basket.Put(ProductsUpdate(products)), isOnline)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .subscribe({
                    basketID = (it as List<Basket>).first().basketId
                }, { stateLiveData.postValue(AppState.Error(it)) })
        )
        else basketID?.let { basket ->
            compositeDisposable.add(
                interactor
                    .getData(Query.Put.Basket.Update(basket, ProductsUpdate(products)), isOnline)
                    .subscribeOn(schedulerProvider.io)
                    .observeOn(schedulerProvider.io)
                    .subscribe({}, { stateLiveData.postValue(AppState.Error(it)) })
            )
        }
        if (basketID == null) stateLiveData.value =
            AppState.Error(RuntimeException("Should init BasketID first"))
    }

    protected fun getRandomPicturesAsPartners(data: MutableList<MainScreenDataModel>) {
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