package ru.sarawan.android.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.sarawan.android.app.App
import javax.inject.Singleton

@Module(
    includes = [
        ActivityModule::class,
        FragmentModule::class,
        ViewModelModule::class,
        NetworkModule::class,
        SharedModule::class,
        SharedLocalStoreModule::class
    ]
)
class AppModule(private val app: App) {

    @Singleton
    @Provides
    fun app(): Context = app
}