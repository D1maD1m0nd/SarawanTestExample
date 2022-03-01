package ru.sarawan.android.di.modules

import dagger.Module
import dagger.Provides
import ru.sarawan.android.model.datasource.*
import ru.sarawan.android.model.interactor.*
import javax.inject.Named

@Module
class InteractorModule {

    @Provides
    fun provideProductInteractor(
        productRepo: ProductDataSource,
        productsRepo: ProductsDataSource,
        categoriesRepo: CategoriesDataSource
    ): ProductInteractor = ProductInteractorImpl(productRepo, productsRepo, categoriesRepo)

    @Provides
    fun provideBasketInteractor(
        @Named(NAME_REMOTE) remoteRepo: BasketDataSource,
        @Named(NAME_LOCAL) localRepo: BasketDataSource,
    ): BasketInteractor = BasketInteractorImpl(remoteRepo, localRepo)

    @Provides
    fun provideOrderInteractor(remoteRepo: OrderDataSource): OrderInteractor =
        OrderInteractorImpl(remoteRepo)

    @Provides
    fun provideUserInteractor(remoteRepo: UserDataSource): UserInteractor =
        UserInteractorImpl(remoteRepo)
}