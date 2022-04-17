package ru.sarawan.android.service.contacts

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface ReceiveMessage {
    fun getMessage(): Observable<String>
    fun firstMessage(): Single<String>
}