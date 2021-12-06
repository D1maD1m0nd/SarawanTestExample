package com.example.sarawan.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sarawan.activity.ActivityViewModel
import com.example.sarawan.framework.ui.basket.viewModel.BasketViewModel
import com.example.sarawan.framework.ui.catalog.viewModel.CatalogViewModel
import com.example.sarawan.framework.ui.category.viewModel.CategoryViewModel
import com.example.sarawan.framework.ui.main.viewModel.MainViewModel
import com.example.sarawan.framework.ui.product_card.viewModel.ProductCardViewModel
import com.example.sarawan.framework.ui.profile.address_fragment.viewModel.ProfileAddressViewModel
import com.example.sarawan.framework.ui.profile.phone_fragment.viewModel.ProfilePhoneViewModel
import com.example.sarawan.framework.ui.profile.sms_code_fragment.viewModel.SmsCodeViewModel
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

    @Binds
    @ViewModelKey(ProductCardViewModel::class)
    @IntoMap
    protected abstract fun productCardViewModel(productCardViewModel: ProductCardViewModel): ViewModel

    @Binds
    @ViewModelKey(ActivityViewModel::class)
    @IntoMap
    protected abstract fun activityViewModel(activityViewModel: ActivityViewModel): ViewModel

    @Binds
    @ViewModelKey(CatalogViewModel::class)
    @IntoMap
    protected abstract fun catalogViewModel(catalogViewModel: CatalogViewModel): ViewModel

    @Binds
    @ViewModelKey(CategoryViewModel::class)
    @IntoMap
    protected abstract fun categoryViewModel(categoryViewModel: CategoryViewModel): ViewModel

    @Binds
    @ViewModelKey(ProfilePhoneViewModel::class)
    @IntoMap
    protected abstract fun profilePhoneViewModel(profilePhoneViewModel: ProfilePhoneViewModel): ViewModel

    @Binds
    @ViewModelKey(SmsCodeViewModel::class)
    @IntoMap
    protected abstract fun smsCodeViewModel(smsCodeViewModel: SmsCodeViewModel): ViewModel

    @Binds
    @ViewModelKey(ProfileAddressViewModel::class)
    @IntoMap
    protected abstract fun profileAddressViewModel(profileAddressViewModel: ProfileAddressViewModel): ViewModel
}

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)