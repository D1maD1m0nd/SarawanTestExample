package ru.sarawan.android.di.modules

import dagger.Binds
import dagger.Module
import ru.sarawan.android.di.annotations.Local
import ru.sarawan.android.model.datasource.basket.BasketDataSource
import ru.sarawan.android.model.datasource.basket.BasketRetrofitImpl
import ru.sarawan.android.model.datasource.basket.BasketRoomImplementation
import ru.sarawan.android.model.datasource.category.CategoriesDataSource
import ru.sarawan.android.model.datasource.category.CategoriesRetrofitImpl
import ru.sarawan.android.model.datasource.map.MapDataSource
import ru.sarawan.android.model.datasource.map.MapRetrofitImpl
import ru.sarawan.android.model.datasource.order.OrderDataSource
import ru.sarawan.android.model.datasource.order.OrderRetrofitImpl
import ru.sarawan.android.model.datasource.product.ProductDataSource
import ru.sarawan.android.model.datasource.product.ProductRetrofitImpl
import ru.sarawan.android.model.datasource.product.ProductsDataSource
import ru.sarawan.android.model.datasource.product.ProductsRetrofitImpl
import ru.sarawan.android.model.datasource.user.UserDataSource
import ru.sarawan.android.model.datasource.user.UserRetrofitImpl

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