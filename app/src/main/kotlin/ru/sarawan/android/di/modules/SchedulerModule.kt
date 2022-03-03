package ru.sarawan.android.di.modules

import dagger.Binds
import dagger.Module
import ru.sarawan.android.rx.ISchedulerProvider
import ru.sarawan.android.rx.SchedulerProvider
import javax.inject.Singleton

@Module
interface SchedulerModule {

    @Suppress("FunctionName")
    @Binds
    @Singleton
    fun bindsSchedulerProvider_to_ISchedulerProvider(schedulerProvider: SchedulerProvider): ISchedulerProvider
}