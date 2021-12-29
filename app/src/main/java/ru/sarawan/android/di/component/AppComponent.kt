package ru.sarawan.android.di.component

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import ru.sarawan.android.app.App
import ru.sarawan.android.di.modules.*
import javax.inject.Singleton

@Component(
    modules = [
        InteractorModule::class,
        ActivityModule::class,
        FragmentModule::class,
        ViewModelModule::class,
        DataSourceModule::class,
        AndroidSupportInjectionModule::class,
        AndroidInjectionModule::class,
        SchedulerModule::class,
        CacheModule::class,
        AppModule::class,
        NetworkModule::class,
        ImageModule::class,
        SharedModule::class,
        StringProviderModule::class
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