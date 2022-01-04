package ru.sarawan.android.di.modules

import dagger.Module
import dagger.Provides
import ru.sarawan.android.model.datasource.ApiService
import ru.sarawan.android.model.datasource.DataSource
import ru.sarawan.android.model.datasource.RetrofitImplementation
import ru.sarawan.android.model.datasource.RoomDataBaseImplementation
import ru.sarawan.android.model.datasource.db.SarawanDatabase
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