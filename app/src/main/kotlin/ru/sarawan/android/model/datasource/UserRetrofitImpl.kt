package ru.sarawan.android.model.datasource

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.address.sarawan.AddressItem
import ru.sarawan.android.model.data.UserDataModel
import ru.sarawan.android.model.data.UserRegistration
import ru.sarawan.android.model.datasource.api.ApiService
import javax.inject.Inject

class UserRetrofitImpl @Inject constructor(
    private val apiService: ApiService
) : UserDataSource {
    override fun getUser(id: Long): Single<UserDataModel> = apiService.getUser(id)

    override fun updateUser(id: Long, user: UserDataModel): Single<UserDataModel> =
        apiService.updateUser(id, user)

    override fun createUser(user: UserRegistration): Single<UserRegistration> =
        apiService.createUser(user)

    override fun sendSMS(user: UserRegistration): Single<UserRegistration> =
        apiService.sendSms(user)

    override fun createAddress(address: AddressItem): Single<AddressItem> =
        apiService.createAddress(address)

    override fun getAddress(): Single<List<AddressItem>> = apiService.getAddress()
}