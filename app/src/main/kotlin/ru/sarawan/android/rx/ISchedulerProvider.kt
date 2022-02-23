package ru.sarawan.android.rx

import io.reactivex.rxjava3.core.Scheduler

interface ISchedulerProvider {

    val ui: Scheduler
    val io: Scheduler
    val computation: Scheduler
}