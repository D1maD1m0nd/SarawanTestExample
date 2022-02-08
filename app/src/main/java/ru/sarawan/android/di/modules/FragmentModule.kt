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
import ru.sarawan.android.framework.ui.profile.address_fragment.ProfileAddressDialogFragment
import ru.sarawan.android.framework.ui.profile.name_fragment.ProfileNameDialogFragment
import ru.sarawan.android.framework.ui.profile.phone_fragment.ProfilePhoneDialogFragment
import ru.sarawan.android.framework.ui.profile.sms_code_fragment.ProfileCodeDialogFragment

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
    abstract fun contributeProfilePhoneFragment() : ProfilePhoneDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileCodeFragment() : ProfileCodeDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileAddressFragment() : ProfileAddressDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileNameFragment() : ProfileNameDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeCategoryFragment() : CategoryFragment

    @ContributesAndroidInjector
    abstract fun contributeOrderFragment() : OrderFragment
}