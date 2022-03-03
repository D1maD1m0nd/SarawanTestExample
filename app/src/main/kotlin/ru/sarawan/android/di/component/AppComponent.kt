package ru.sarawan.android.di.component

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import ru.sarawan.android.app.App
import ru.sarawan.android.di.modules.AppModule
import javax.inject.Singleton

@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AndroidInjectionModule::class,
        AppModule::class,
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