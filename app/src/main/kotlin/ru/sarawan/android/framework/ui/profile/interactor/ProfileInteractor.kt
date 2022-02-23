package ru.sarawan.android.framework.ui.profile.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.UserDataModel

class ProfileInteractor : IProfileInteractor {
    override fun formatPhone(number: String, numberMask: String): Single<String> {
        var index = 0
        var result = numberMask
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
        return Single.just(result)
    }

    override fun formatName(user: UserDataModel, emptyStr: String): Single<String> {
        val firstName = user.firstName
        val lastName = user.lastName
        val fullName = "$firstName $lastName".trim()
        val result = fullName.ifEmpty { emptyStr }
        return Single.just(result)
    }

}