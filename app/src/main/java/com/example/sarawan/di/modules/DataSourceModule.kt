package com.example.sarawan.di.modules

import com.example.sarawan.model.datasource.ApiService
import com.example.sarawan.model.datasource.DataSource
import com.example.sarawan.model.datasource.RetrofitImplementation
import com.example.sarawan.model.datasource.RoomDataBaseImplementation
import com.example.sarawan.model.datasource.db.SarawanDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class DataSourceModule {

    @Provides
    @Singleton
    @Named(NAME_REMOTE)
    internal fun provideRemoteDataSource(apiService: ApiService): DataSource<List<*>> {
        return RetrofitImplementation(apiService)
    }

    @Provides
    @Singleton
    @Named(NAME_LOCAL)
    internal fun provideLocalDataSource(db: SarawanDatabase): DataSource<List<*>> {
        return RoomDataBaseImplementation(db)
    }


}