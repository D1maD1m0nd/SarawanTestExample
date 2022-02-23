package ru.sarawan.android.di.modules

import android.content.Context
import coil.ImageLoader
import dagger.Module
import dagger.Provides
import ru.sarawan.android.R
import javax.inject.Singleton

@Module
class ImageModule {

    @Provides
    @Singleton
    fun provideCoilLoader(context: Context): ImageLoader =
        ImageLoader.Builder(context)
            .error(R.drawable.place_holder_image)
            .placeholder(R.drawable.place_holder_image)
            .crossfade(true)
            .build()

}