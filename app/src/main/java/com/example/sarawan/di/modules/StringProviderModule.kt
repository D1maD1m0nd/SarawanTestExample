package com.example.sarawan.di.modules

import android.content.Context
import com.example.sarawan.utils.StringProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class StringProviderModule {

    @Provides
    @Singleton
    fun stringProvider(context: Context): StringProvider = StringProvider(context)
}