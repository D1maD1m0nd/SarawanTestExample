package com.example.sarawan.di.component

import android.app.Application
import com.example.sarawan.app.App
import com.example.sarawan.di.modules.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Component(
    modules = [
        InteractorModule::class,
        ActivityModule::class,
        FragmentModule::class,
        ViewModelModule::class,
        RepoModule::class,
        AndroidSupportInjectionModule::class,
        AndroidInjectionModule::class,
        SchedulerModule::class,
        CacheModule::class,
        AppModule::class,
        NetworkModule::class,
        ImageModule::class
    ]
)
@Singleton
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(app: Application): Builder

        fun appModule(appModule: AppModule): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)
}