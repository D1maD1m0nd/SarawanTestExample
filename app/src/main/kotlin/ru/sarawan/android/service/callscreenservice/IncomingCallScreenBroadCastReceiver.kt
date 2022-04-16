package ru.sarawan.android.service.callscreenservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import ru.sarawan.android.service.contacts.ReceiveMessage

class IncomingCallScreenBroadCastReceiver : BroadcastReceiver(), ReceiveMessage {
    private val statusSubject: BehaviorSubject<String> = BehaviorSubject.create()

    override fun onReceive(context: Context?, intent: Intent?) {
        val phoneNumber = intent?.getStringExtra(IncomingCallScreenService.PHONE_NUMBER)
        statusSubject.onNext(phoneNumber)
    }

    override fun getMessage(): Observable<String> = statusSubject

    override fun firstMessage(): Single<String> = statusSubject.first("")
}