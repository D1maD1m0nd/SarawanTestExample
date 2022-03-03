package ru.sarawan.android.di.modules

import dagger.Binds
import dagger.Module
import ru.sarawan.android.model.interactor.*

@Suppress("FunctionName")
@Module(includes = [DataSourceModule::class])
interface InteractorModule {

    @Binds
    fun bindsProductInteractorImpl_to_ProductInteractor(productInteractorImpl: ProductInteractorImpl): ProductInteractor

    @Binds
    fun bindsBasketInteractorImpl_to_BasketInteractor(BasketInteractorImpl: BasketInteractorImpl): BasketInteractor

    @Binds
    fun bindsOrderInteractorImpl_to_OrderInteractor(orderInteractorImpl: OrderInteractorImpl): OrderInteractor

    @Binds
    fun bindsUserInteractorImpl_to_UserInteractor(userInteractorImpl: UserInteractorImpl): UserInteractor
}