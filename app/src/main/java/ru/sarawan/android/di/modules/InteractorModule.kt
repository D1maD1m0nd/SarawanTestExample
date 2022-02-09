package ru.sarawan.android.di.modules

import dagger.Module
import dagger.Provides
import ru.sarawan.android.framework.MainInteractor
import ru.sarawan.android.framework.ui.basket.interactor.BasketInteractor
import ru.sarawan.android.framework.ui.basket.interactor.IBasketInteractor
import ru.sarawan.android.framework.ui.profile.interactor.IProfileInteractor
import ru.sarawan.android.framework.ui.profile.interactor.ProfileInteractor
import ru.sarawan.android.model.datasource.DataSource
import javax.inject.Named

@Module
class InteractorModule {

    @Provides
    fun provideInteractor(
        @Named(NAME_REMOTE) remoteRepo: DataSource<List<*>>,
        @Named(NAME_LOCAL) localRepo: DataSource<List<*>>,
    ): MainInteractor {
        return MainInteractor(remoteRepo, localRepo)
    }


    @Provides
    fun provideBasketInteractor(): IBasketInteractor {
        return BasketInteractor()
    }

    @Provides
    fun provideProfileInteractor(): IProfileInteractor {
        return ProfileInteractor()
    }
}