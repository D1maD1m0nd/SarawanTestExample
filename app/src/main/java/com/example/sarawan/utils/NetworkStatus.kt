package com.example.sarawan.utils

import io.reactivex.rxjava3.core.Observable

interface NetworkStatus {
    fun isOnline(): Observable<Boolean>
}