package ru.sarawan.android.model.datasource.user

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.UserDataModel
import ru.sarawan.android.model.data.UserRegistration
import ru.sarawan.android.model.data.address.sarawan.AddressItem

interface UserDataSource {
    fun getUser(id: Long): Single<UserDataModel>
    fun updateUser(id: Long, user: UserDataModel): Single<UserDataModel>
    fun createUser(user: UserRegistration): Single<UserRegistration>
    fun sendSMS(user: UserRegistration): Single<UserRegistration>
    fun createAddress(address: AddressItem): Single<AddressItem>
    fun getAddress(): Single<List<AddressItem>>
}