package ru.sarawan.android.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.sarawan.android.framework.ui.basket.BasketFragment
import ru.sarawan.android.framework.ui.catalog.CatalogFragment
import ru.sarawan.android.framework.ui.category.CategoryFragment
import ru.sarawan.android.framework.ui.main.MainFragment
import ru.sarawan.android.framework.ui.map.MapFragment
import ru.sarawan.android.framework.ui.order.OrderFragment
import ru.sarawan.android.framework.ui.product_card.ProductCardFragment
import ru.sarawan.android.framework.ui.profile.ProfileFragment
import ru.sarawan.android.framework.ui.profile.address_fragment.ProfileAddressDialogFragment
import ru.sarawan.android.framework.ui.profile.name_fragment.ProfileNameDialogFragment
import ru.sarawan.android.framework.ui.profile.phone_fragment.ProfilePhoneDialogFragment
import ru.sarawan.android.framework.ui.profile.sms_code_fragment.ProfileCodeDialogFragment

@Module(includes = [ImageModule::class])
interface FragmentModule {

    @ContributesAndroidInjector
    fun contributeMainFragment(): MainFragment

    @ContributesAndroidInjector
    fun contributeCatalogFragment(): CatalogFragment

    @ContributesAndroidInjector
    fun contributeBasketFragment(): BasketFragment

    @ContributesAndroidInjector
    fun contributeProductCardFragment(): ProductCardFragment

    @ContributesAndroidInjector
    fun contributeProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    fun contributeProfilePhoneFragment(): ProfilePhoneDialogFragment

    @ContributesAndroidInjector
    fun contributeProfileCodeFragment(): ProfileCodeDialogFragment

    @ContributesAndroidInjector
    fun contributeProfileAddressFragment(): ProfileAddressDialogFragment

    @ContributesAndroidInjector
    fun contributeProfileNameFragment(): ProfileNameDialogFragment

    @ContributesAndroidInjector
    fun contributeCategoryFragment(): CategoryFragment

    @ContributesAndroidInjector
    fun contributeOrderFragment(): OrderFragment

    @ContributesAndroidInjector
    fun contributeMapFragment(): MapFragment
}