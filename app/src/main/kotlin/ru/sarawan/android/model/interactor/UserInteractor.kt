package ru.sarawan.android.model.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.AddressItem
import ru.sarawan.android.model.data.UserDataModel
import ru.sarawan.android.model.data.UserRegistration
import ru.sarawan.android.utils.constants.AddressState

interface UserInteractor {
    fun getUser(id: Long): Single<UserDataModel>
    fun updateUser(id: Long, user: UserDataModel): Single<UserDataModel>
    fun createUser(user: UserRegistration): Single<UserRegistration>
    fun sendSMS(user: UserRegistration): Single<UserRegistration>
    fun createAddress(address: AddressItem): Single<AddressItem>
    fun getAddress(): Single<List<AddressItem>>
    fun formatPhone(number: String, numberMask: String): Single<String>
    fun formatName(user: UserDataModel, emptyStr: String): Single<String>
    fun validateAddress(addressItem: AddressItem): Single<AddressState>
}