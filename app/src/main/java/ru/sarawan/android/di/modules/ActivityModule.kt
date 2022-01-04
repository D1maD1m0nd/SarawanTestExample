package ru.sarawan.android.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): ru.sarawan.android.activity.MainActivity
}