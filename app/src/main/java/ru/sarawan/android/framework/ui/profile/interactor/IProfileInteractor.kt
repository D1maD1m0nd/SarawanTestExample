package ru.sarawan.android.framework.ui.profile.interactor

import io.reactivex.rxjava3.core.Single
import ru.sarawan.android.model.data.UserDataModel

interface IProfileInteractor {
    fun formatPhone(number: String, numberMask: String): Single<String>
    fun formatName(user: UserDataModel, emptyStr: String): Single<String>
}