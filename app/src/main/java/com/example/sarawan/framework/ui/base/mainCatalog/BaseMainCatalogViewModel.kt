package com.example.sarawan.framework.ui.base.mainCatalog

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sarawan.R
import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.BaseViewModel
import com.example.sarawan.model.data.*
import com.example.sarawan.rx.ISchedulerProvider
import io.reactivex.rxjava3.core.Single

abstract class BaseMainCatalogViewModel(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>(), MainCatalogInterface {

    protected val stateMoreLiveData: MutableLiveData<AppState<*>> = MutableLiveData()

    private var lastPage = 1

    protected var searchWord: String? = null

    protected var basketID: Int? = null

    fun getMoreLiveData(): LiveData<AppState<*>> = stateMoreLiveData

    fun search(word: String, isOnline: Boolean) {
        lastPage = 1
        searchWord = word
        compositeDisposable.add(
            loadMoreData(isOnline, Query.Get.Products.ProductName(word))
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .doOnSubscribe { stateLiveData.postValue(AppState.Loading) }
                .subscribe(
                    { stateLiveData.postValue(AppState.Success(it)) },
                    { stateLiveData.postValue(AppState.Error(it)) }
                )
        )
    }

    fun saveData(data: MainScreenDataModel, isOnline: Boolean, isNewItem: Boolean) {
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

    protected fun loadMoreData(
        isOnline: Boolean,
        query: Query.Get.Products
    ): Single<MutableList<MainScreenDataModel>> {
        val basket = interactor.getData(Query.Get.Basket, isOnline).onErrorReturnItem(listOf(Basket()))
        val productsData = getProductsData(isOnline, query.apply { page = lastPage })
            .onErrorReturnItem(mutableListOf())
        return Single.zip(productsData, basket) { productData, basketData ->
            val data: MutableList<MainScreenDataModel> = mutableListOf()
            val basketObject = (basketData as List<Basket>).firstOrNull()
            basketID = basketObject?.basketId
            productData.forEach { singleData ->
                val mainScreenData = (singleData).toMainScreenDataModel()
                data.add(mainScreenData)
                basketObject?.products?.forEach { basketSingleData ->
                    if (mainScreenData.id == basketSingleData.basketProduct?.basketProduct?.id)
                        mainScreenData.quantity = basketSingleData.quantity
                }
            }
            lastPage += PAGES
            data
        }
    }

    private fun getProductsData(
        isOnline: Boolean,
        query: Query.Get.Products
    ): Single<MutableList<Product>> {
        return if (lastPage + PAGES <= query.page) Single.fromCallable { mutableListOf() }
        else Single.zip(
            interactor.getData(
                query,
                isOnline
            ) as Single<MutableList<Product>>,
            getProductsData(isOnline, query.apply { page += 1 })
        ) { data1, data2 ->
            data1.addAll(data2)
            data1
        }
    }

    abstract fun getMoreData(isOnline: Boolean)

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

    companion object {
        const val PAGES = 5
    }
}