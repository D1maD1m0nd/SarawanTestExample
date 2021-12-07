package com.example.sarawan.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.navigation.NavController
import com.example.sarawan.di.component.DaggerAppComponent
import com.example.sarawan.di.modules.AppModule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class App : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.builder()
            .application(this)
            .appModule(AppModule(this))
            .build()
            .inject(this)
    }
    companion object{
        lateinit var navController : NavController
    }

}