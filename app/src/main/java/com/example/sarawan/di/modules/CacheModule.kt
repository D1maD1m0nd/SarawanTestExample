package com.example.sarawan.di.modules

import android.content.Context
import androidx.room.Room
import com.example.sarawan.model.datasource.db.SarawanDatabase
import dagger.Module
import dagger.Provides
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

    companion object {

        private const val DB_NAME = "database.db"
    }
}