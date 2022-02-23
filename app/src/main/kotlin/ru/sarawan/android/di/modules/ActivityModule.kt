package ru.sarawan.android.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.sarawan.android.activity.MainActivity

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity
}