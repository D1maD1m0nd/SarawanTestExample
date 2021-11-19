package com.example.sarawan.framework.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sarawan.model.data.AppState
import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class BaseViewModel<T : AppState<*>>(
    protected val stateLiveData: MutableLiveData<T> = MutableLiveData()
) : ViewModel() {

    protected val compositeDisposable = CompositeDisposable()

    fun getStateLiveData(): LiveData<T> = stateLiveData

    override fun onCleared() {
        compositeDisposable.clear()
    }
}