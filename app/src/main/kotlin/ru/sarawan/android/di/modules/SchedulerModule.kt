package ru.sarawan.android.di.modules

import dagger.Module
import dagger.Provides
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.rx.SchedulerProvider
import javax.inject.Singleton

@Module
class SchedulerModule {

    @Provides
    @Singleton
    fun schedulerProvider(): ISchedulerProvider = SchedulerProvider()
}