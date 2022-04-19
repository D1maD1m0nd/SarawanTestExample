package ru.sarawan.android.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import ru.sarawan.android.service.contacts.ReceiveMessage

class IncomingCallReaderService : BroadcastReceiver(), ReceiveMessage {
    private var phoneNumber: String? = null
    private val statusSubject: BehaviorSubject<String> = BehaviorSubject.create()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.getStringExtra(TelephonyManager.EXTRA_STATE)
                .equals(TelephonyManager.EXTRA_STATE_RINGING)
        ) {
            Log.i("SERVICE", intent?.dataString.toString())
        }
        val telephonyManager: TelephonyManager = context
            ?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        // TODO: На API > 30 перехват входящего номера не работает
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            telephonyManager.listen(
                object : PhoneStateListener() {
                    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                        if (!phoneNumber.isNullOrEmpty()) {
                            this@IncomingCallReaderService.phoneNumber = phoneNumber
                            statusSubject.onNext(phoneNumber)
                        }
                    }
                }, PhoneStateListener.LISTEN_CALL_STATE
            )
        }
    }

    override fun getMessage(): Observable<String> = statusSubject
    override fun firstMessage(): Single<String> = statusSubject.first("")
}