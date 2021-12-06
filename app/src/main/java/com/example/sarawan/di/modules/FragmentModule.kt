package com.example.sarawan.di.modules

import com.example.sarawan.framework.ui.basket.BasketFragment
import com.example.sarawan.framework.ui.catalog.CatalogFragment
import com.example.sarawan.framework.ui.main.MainFragment
import com.example.sarawan.framework.ui.product_card.ProductCardFragment
import com.example.sarawan.framework.ui.profile.ProfileFragment
import com.example.sarawan.framework.ui.profile.address_fragment.ProfileAddressFragment
import com.example.sarawan.framework.ui.profile.phone_fragment.ProfilePhoneFragment
import com.example.sarawan.framework.ui.profile.sms_code_fragment.ProfileCodeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

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
}