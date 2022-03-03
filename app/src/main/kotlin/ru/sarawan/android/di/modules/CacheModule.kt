package ru.sarawan.android.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.sarawan.android.model.datasource.db.SarawanDatabase
import javax.inject.Singleton

@Module
class CacheModule {

    @Provides
    @Singleton
    fun db(context: Context): SarawanDatabase {
        return Room.databaseBuilder(context, SarawanDatabase::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }
}