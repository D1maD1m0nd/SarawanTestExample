package ru.sarawan.android.service.callscreenservice

import android.content.Intent
import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import androidx.annotation.RequiresApi
import io.reactivex.rxjava3.subjects.BehaviorSubject

@RequiresApi(Build.VERSION_CODES.N)
class IncomingCallScreenService : CallScreeningService() {
    private val incomingCallScreenStatusObject: BehaviorSubject<String> = BehaviorSubject.create()
    override fun onScreenCall(callDetails: Call.Details) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (callDetails.callDirection == Call.Details.DIRECTION_INCOMING) {
                Log.e("phone service", callDetails.handle.schemeSpecificPart)
                val phoneNumber: String = callDetails.handle.schemeSpecificPart
                val intent = Intent()
                intent.action = INTENT_INCOMING_CALL_ACTION
                intent.putExtra(PHONE_NUMBER, phoneNumber)
                sendBroadcast(intent)
            }
        }
    }

    companion object {
        const val PHONE_NUMBER = "phoneNumber"
        const val INTENT_INCOMING_CALL_ACTION = "incoming_call_action"
    }
}