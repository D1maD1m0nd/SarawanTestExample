package com.example.sarawan.di.modules

import com.example.sarawan.rx.ISchedulerProvider
import com.example.sarawan.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SchedulerModule {

    @Provides
    @Singleton
    fun schedulerProvider(): ISchedulerProvider = SchedulerProvider()
}