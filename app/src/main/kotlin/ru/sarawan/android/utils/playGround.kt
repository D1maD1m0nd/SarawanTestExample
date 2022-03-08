package ru.sarawan.android.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
fun main(args: Array<String>) {
    diffTime("12:00", "13:00", 14, "11:00")

}

@RequiresApi(Build.VERSION_CODES.O)
private fun diffTime(): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    var timeDepartmentString = readLine()!!
    var timeArrivalString = readLine()!!
    val timeZoneString = readLine()!!

    val timeZoneInt = abs(timeZoneString.toInt())

    if (timeDepartmentString.length < 5) timeDepartmentString = "0$timeDepartmentString"
    val timeDepartment = LocalTime.parse(timeDepartmentString, formatter)

    if (timeArrivalString.length < 5) timeArrivalString = "0$timeArrivalString"
    val timeArrival = LocalTime.parse(timeArrivalString, formatter).plusHours(timeZoneInt.toLong())

    var duration = Duration.between(timeDepartment, timeArrival).toMinutes()
    if (duration < 0) duration += 24 * 60

    return String.format("%d:%02d", duration / 60, (duration % 60))
}

@RequiresApi(Build.VERSION_CODES.O)
private fun diffTime(t1 : String, t2 : String, tZone : Int, result : String): Boolean {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    var timeDepartmentString = t1
    var timeArrivalString = t2

    val timeZoneInt = abs(tZone)

    if (timeDepartmentString.length < 5) timeDepartmentString = "0$timeDepartmentString"
    val timeDepartment = LocalTime.parse(timeDepartmentString, formatter)

    if (timeArrivalString.length < 5) timeArrivalString = "0$timeArrivalString"
    val timeArrival = LocalTime.parse(timeArrivalString, formatter).plusHours(timeZoneInt.toLong())

    var duration = Duration.between(timeDepartment, timeArrival).toMinutes()
    if (duration < 0) duration += 24 * 60
    val timeDiff =  String.format("%d:%02d", duration / 60, (duration % 60))
    val res = timeDiff == result
    println("$timeDiff == $res")
    return res
}