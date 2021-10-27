package com.example.sarawan.di.modules

import com.example.sarawan.framework.ui.basket.BasketFragment
import com.example.sarawan.framework.ui.main.MainFragment
import com.example.sarawan.framework.ui.profile.ProfileFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment

    @ContributesAndroidInjector
    abstract fun contributeBasketFragment(): BasketFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileFragment(): ProfileFragment
}