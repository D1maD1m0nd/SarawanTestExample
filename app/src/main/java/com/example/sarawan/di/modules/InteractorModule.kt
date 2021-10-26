package com.example.sarawan.di.modules

import com.example.sarawan.framework.MainInteractor
import com.example.sarawan.model.data.DataModel
import com.example.sarawan.model.repository.Repository
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class InteractorModule {

    @Provides
    fun provideInteractor(
        @Named(NAME_REMOTE) remoteRepo: Repository<List<DataModel>>,
        @Named(NAME_LOCAL) localRepo: Repository<List<DataModel>>,
    ): MainInteractor {
        return MainInteractor(remoteRepo, localRepo)
    }
}