package com.example.sarawan.di.modules

import com.example.sarawan.MainActivity
import com.example.sarawan.framework.ui.main.MainFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity
}