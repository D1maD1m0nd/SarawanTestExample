package com.example.sarawan.di.modules

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SharedModule {

    @Provides
    @Singleton
    fun sharedProvider(context: Context): SharedPreferences =
        context.getSharedPreferences(SHARED, Context.MODE_PRIVATE)
}