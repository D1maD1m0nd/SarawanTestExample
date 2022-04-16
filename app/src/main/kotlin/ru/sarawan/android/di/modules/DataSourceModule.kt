package ru.sarawan.android.di.modules

import dagger.Binds
import dagger.Module
import ru.sarawan.android.di.annotations.Local
import ru.sarawan.android.model.datasource.*

@Suppress("FunctionName")
@Module(includes = [CacheModule::class])
interface DataSourceModule {

    @Binds
    fun bindBasketRetrofitImpl_to_BasketDataSource(basketRetrofitImpl: BasketRetrofitImpl): BasketDataSource

    @Binds
    @Local
    fun bindBasketRoomImplementation_to_BasketDataSource(basketRoomImplementation: BasketRoomImplementation): BasketDataSource

    @Binds
    fun bindCategoriesImpl_to_CategoriesRetrofitSource(categoriesRetrofitImpl: CategoriesRetrofitImpl): CategoriesDataSource

    @Binds
    fun bindOrderImpl_to_OrderRetrofitSource(orderRetrofitImpl: OrderRetrofitImpl): OrderDataSource

    @Binds
    fun bindProductImpl_to_ProductRetrofitSource(productRetrofitImpl: ProductRetrofitImpl): ProductDataSource

    @Binds
    fun bindProductsImpl_to_ProductsRetrofitSource(productsRetrofitImpl: ProductsRetrofitImpl): ProductsDataSource

    @Binds
    fun provideUserImpl_to_UserRetrofitSource(userRetrofitImpl: UserRetrofitImpl): UserDataSource

    @Binds
    fun provideMapImpl_to_MapRetrofitSource(mapImpl: MapRetrofitImpl): MapDataSource
}