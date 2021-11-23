package com.example.sarawan.framework.ui.basket.viewModel

import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.framework.ui.base.BaseViewModel
import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.Basket
import com.example.sarawan.model.datasource.repository.Repository
import com.example.sarawan.rx.ISchedulerProvider
import io.reactivex.rxjava3.observers.DisposableObserver
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class BasketViewModel @Inject constructor(
    private val interactor: MainInteractor,
    private val schedulerProvider: ISchedulerProvider
) : BaseViewModel<AppState<*>>() {
    private val callBack = object :
        Callback<Basket> {
        override fun onResponse(call: Call<Basket>, response: Response<Basket>) {
            val serverResponse: Basket? = response.body()
            stateLiveData.postValue(
                if (response.isSuccessful && serverResponse != null) {
                    checkResponse(serverResponse)
                } else {
                    AppState.Error(Throwable(response.code().toString()))
                }
            )
        }

        override fun onFailure(call: Call<Basket>, t: Throwable) {
            stateLiveData.postValue(AppState.Error(Throwable(t.message)))
        }

        private fun checkResponse(serverResponse: Basket): AppState.BasketSuccess<Basket> {
            return AppState.BasketSuccess(serverResponse)
        }

    }
    var repository : Repository? = null
    fun search(word: String, isOnline: Boolean) {
        compositeDisposable.add(
            interactor.getData(word, isOnline)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.io)
                .doOnSubscribe { stateLiveData.postValue(AppState.Loading) }
                .subscribeWith(getObserver())
        )
    }
    fun getBasket() {
        repository?.getBasket(callBack)
    }
    private fun getObserver() = object : DisposableObserver<AppState<*>>() {
        override fun onNext(appState: AppState<*>) {
            stateLiveData.postValue(appState)
        }

        override fun onError(e: Throwable) {
            stateLiveData.postValue(AppState.Error(e))
        }

        override fun onComplete() = Unit
    }
    companion object {

    }
}