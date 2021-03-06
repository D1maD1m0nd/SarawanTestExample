package ru.sarawan.android.model.interactor

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.operators.single.SingleJust
import ru.sarawan.android.model.data.UserDataModel
import ru.sarawan.android.model.data.UserRegistration
import ru.sarawan.android.model.data.address.sarawan.AddressItem
import ru.sarawan.android.model.datasource.user.UserDataSource
import ru.sarawan.android.utils.constants.AddressState
import javax.inject.Inject

class UserInteractorImpl @Inject constructor(
    private val remoteRepository: UserDataSource
) : UserInteractor {
    override fun getUser(id: Long): Single<UserDataModel> = remoteRepository.getUser(id)

    override fun updateUser(id: Long, user: UserDataModel): Single<UserDataModel> =
        remoteRepository.updateUser(id, user)

    override fun createUser(user: UserRegistration): Single<UserRegistration> =
        remoteRepository.createUser(user)

    override fun sendSMS(user: UserRegistration): Single<UserRegistration> =
        remoteRepository.sendSMS(user)


    override fun createAddress(address: AddressItem): Single<AddressItem> =
        remoteRepository.createAddress(address)

    override fun getAddress(): Single<List<AddressItem>> {
        return remoteRepository.getAddress().flatMap {
            SingleJust(it.map { item ->
                val city = item.city
                val street = item.street
                val house = item.house
                val roomNum = item.roomNumber
                item.addressFull = "$city, ул $street, д $house, кв $roomNum"
                item
            })
        }
    }

    override fun formatAddress(address: AddressItem): Single<String> {
        val city = address.city
        val street = address.street
        val house = address.house
        val roomNum = address.roomNumber
        return Single.just("$city, ул $street, д $house, кв $roomNum")

    }

    override fun formatPhone(number: String, numberMask: String): Single<String> {
        var index = 0
        val result = numberMask
            .asSequence()
            .map { c ->
                if (index < number.length) {
                    val cc = number[index]
                    if (cc == c || c == '9') {
                        index++
                        cc
                    } else c
                } else c
            }.joinToString("")
        return SingleJust(result)
    }

    override fun formatName(user: UserDataModel, emptyStr: String): Single<String> {
        val firstName = user.firstName
        val lastName = user.lastName
        val fullName = "$firstName $lastName".trim()
        val result = fullName.ifEmpty { emptyStr }
        return SingleJust(result)
    }

    override fun validateAddress(addressItem: AddressItem): Single<AddressState> {
        val city = addressItem.city
        val street = addressItem.street
        val house = addressItem.house
        val roomNumber = addressItem.roomNumber
        val state = when {
            city.isEmpty() -> {
                AddressState.CITY
            }
            street.isEmpty() -> {
                AddressState.STREET
            }
            house.isEmpty() -> {
                AddressState.HOUSE
            }
            roomNumber.isEmpty() -> {
                AddressState.NUMBER
            }
            else -> {
                AddressState.VALID
            }
        }
        return Single.just(state)
    }

}