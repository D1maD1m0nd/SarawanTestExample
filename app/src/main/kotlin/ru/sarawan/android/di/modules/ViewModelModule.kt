package ru.sarawan.android.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import ru.sarawan.android.activity.ActivityViewModel
import ru.sarawan.android.framework.ui.basket.viewModel.BasketViewModel
import ru.sarawan.android.framework.ui.catalog.viewModel.CatalogViewModel
import ru.sarawan.android.framework.ui.category.viewModel.CategoryViewModel
import ru.sarawan.android.framework.ui.main.viewModel.MainViewModel
import ru.sarawan.android.framework.ui.map.viewModel.MapViewModel
import ru.sarawan.android.framework.ui.order.viewModel.OrderViewModel
import ru.sarawan.android.framework.ui.product_card.viewModel.ProductCardViewModel
import ru.sarawan.android.framework.ui.profile.address_fragment.viewModel.ProfileAddressViewModel
import ru.sarawan.android.framework.ui.profile.name_fragment.viewModel.NameViewModel
import ru.sarawan.android.framework.ui.profile.phone_fragment.viewModel.ProfilePhoneViewModel
import ru.sarawan.android.framework.ui.profile.sms_code_fragment.viewModel.SmsCodeViewModel
import ru.sarawan.android.framework.ui.profile.viewModel.ProfileViewModel
import kotlin.reflect.KClass

@Module(includes = [InteractorModule::class, SchedulerModule::class])
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

    @Binds
    @ViewModelKey(ProfileViewModel::class)
    @IntoMap
    protected abstract fun profileViewModel(profileViewModel: ProfileViewModel): ViewModel

    @Binds
    @ViewModelKey(NameViewModel::class)
    @IntoMap
    protected abstract fun nameViewModel(nameViewModel: NameViewModel): ViewModel

    @Binds
    @ViewModelKey(OrderViewModel::class)
    @IntoMap
    protected abstract fun orderViewModel(orderViewModel: OrderViewModel): ViewModel

    @Binds
    @ViewModelKey(MapViewModel::class)
    @IntoMap
    protected abstract fun mapViewModel(mapViewModel: MapViewModel): ViewModel
}

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)