package com.example.sarawan.framework

import com.example.sarawan.model.data.AppState
import com.example.sarawan.model.data.DataModel
import com.example.sarawan.model.repository.Repository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class MainInteractor @Inject constructor(
    private val remoteRepository: Repository<List<DataModel>>,
    private val localRepository: Repository<List<DataModel>>
) : Interactor<AppState> {

    override fun getData(word: String, fromRemoteSource: Boolean): Observable<AppState> {
        return if (fromRemoteSource) {
            remoteRepository.getData(word).map {
                localRepository.saveData(it)
                AppState.Success(it)
            }
        } else {
            localRepository.getData(word).map { AppState.Success(it) }
        }
    }
}
