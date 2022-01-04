package ru.sarawan.android.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.sarawan.android.framework.ui.basket.BasketFragment
import ru.sarawan.android.framework.ui.catalog.CatalogFragment
import ru.sarawan.android.framework.ui.category.CategoryFragment
import ru.sarawan.android.framework.ui.main.MainFragment
import ru.sarawan.android.framework.ui.order.OrderFragment
import ru.sarawan.android.framework.ui.product_card.ProductCardFragment
import ru.sarawan.android.framework.ui.profile.ProfileFragment
import ru.sarawan.android.framework.ui.profile.address_fragment.ProfileAddressFragment
import ru.sarawan.android.framework.ui.profile.name_fragment.ProfileNameFragment
import ru.sarawan.android.framework.ui.profile.phone_fragment.ProfilePhoneFragment
import ru.sarawan.android.framework.ui.profile.sms_code_fragment.ProfileCodeFragment

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment

    @ContributesAndroidInjector
    abstract fun contributeCatalogFragment(): CatalogFragment

    @ContributesAndroidInjector
    abstract fun contributeBasketFragment(): BasketFragment

    @ContributesAndroidInjector
    abstract fun contributeProductCardFragment(): ProductCardFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun contributeProfilePhoneFragment() : ProfilePhoneFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileCodeFragment() : ProfileCodeFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileAddressFragment() : ProfileAddressFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileNameFragment() : ProfileNameFragment

    @ContributesAndroidInjector
    abstract fun contributeCategoryFragment() : CategoryFragment

    @ContributesAndroidInjector
    abstract fun contributeOrderFragment() : OrderFragment
}