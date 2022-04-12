package ru.sarawan.android.di.modules

import dagger.Binds
import dagger.Module
import ru.sarawan.android.utils.exstentions.localstore.LocalStore
import ru.sarawan.android.utils.exstentions.localstore.SharedPrefStore
import javax.inject.Singleton
@Module
interface SharedLocalStoreModule {
    @Binds
    @Singleton
    fun bindSharePreferenceLocalStore(localStore: SharedPrefStore):LocalStore
}