package com.example.sarawan.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sarawan.framework.ui.basket.viewModel.BasketViewModel
import com.example.sarawan.framework.ui.main.viewModel.MainViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@Module(includes = [InteractorModule::class])
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @ViewModelKey(MainViewModel::class)
    @IntoMap
    protected abstract fun mainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @ViewModelKey(BasketViewModel::class)
    @IntoMap
    protected abstract fun basketViewModel(basketViewModel: BasketViewModel): ViewModel
}

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)