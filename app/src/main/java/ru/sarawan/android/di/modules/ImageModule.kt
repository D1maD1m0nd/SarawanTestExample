package ru.sarawan.android.di.modules

import android.content.Context
import coil.ImageLoader
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ImageModule {

    @Provides
    @Singleton
    fun provideCoilLoader(context: Context): ImageLoader =
        ImageLoader.Builder(context)
            .crossfade(true)
            .build()

}