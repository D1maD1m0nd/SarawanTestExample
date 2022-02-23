package ru.sarawan.android.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.sarawan.android.utils.StringProvider
import javax.inject.Singleton

@Module
class StringProviderModule {

    @Provides
    @Singleton
    fun stringProvider(context: Context): StringProvider = StringProvider(context)
}