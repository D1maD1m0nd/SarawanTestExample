package ru.sarawan.android.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.sarawan.android.activity.MainActivity

@Module
interface ActivityModule {

    @ContributesAndroidInjector
    fun contributeMainActivity(): MainActivity
}