package ru.sarawan.android.di.modules

import dagger.Module
import dagger.Provides
import ru.sarawan.android.model.datasource.*
import ru.sarawan.android.model.datasource.db.SarawanDatabase
import javax.inject.Named

@Module
class DataSourceModule {

    @Provides
    @Named(NAME_REMOTE)
    fun provideRemoteBasketSource(apiService: ApiService): BasketDataSource =
        BasketRetrofitImpl(apiService)

    @Provides
    @Named(NAME_LOCAL)
    fun provideLocalBasketSource(db: SarawanDatabase): BasketDataSource =
        BasketRoomImplementation(db)

    @Provides
    fun provideCategoriesSource(apiService: ApiService): CategoriesDataSource =
        CategoriesRetrofitImpl(apiService)

    @Provides
    fun provideOrderSource(apiService: ApiService): OrderDataSource =
        OrderRetrofitImpl(apiService)

    @Provides
    fun provideProductSource(apiService: ApiService): ProductDataSource =
        ProductRetrofitImpl(apiService)

    @Provides
    fun provideProductsSource(apiService: ApiService): ProductsDataSource =
        ProductsRetrofitImpl(apiService)

    @Provides
    fun provideUserSource(apiService: ApiService): UserDataSource =
        UserRetrofitImpl(apiService)
}