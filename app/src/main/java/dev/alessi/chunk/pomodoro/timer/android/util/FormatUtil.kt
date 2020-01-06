package dev.alessi.chunk.pomodoro.timer.android.util

import android.text.format.DateUtils

fun Long.toFormatedTime(): String {
    return DateUtils.formatElapsedTime(this / 1000)
}

fun Long.toFormatedMinutes(): String {

    val minutes = (this / 60000)
    val rMinutes = minutes.rem(100)

    val hours = (this / (60000 * 60))

    return if (minutes < 100)
        "${rMinutes.toString().padStart(2, '0')} min"
    else
        "${hours}h ${minutes.rem(60).toString().padStart(2, '0')} min"

}


fun Long.toFormatedElapsedInMinutes(): String {
    val rSeconds = (this / 1000).rem(60)
    val minutes = (this / 60000)
    val rMinutes = minutes.rem(100)

    val hours = (this / (60000 * 60))

    println("minutes: $minutes")
    return if (minutes < 100)
        "${rMinutes.toString().padStart(2, '0')}:${rSeconds.toString().padStart(2, '0')}"
    else
        "${hours}:${minutes.rem(60).toString().padStart(2, '0')}:${rSeconds.toString().padStart(2, '0')}"


}

//fun Int.toFormatedTime(): String {
//    val seconds = this * 60
//
//
//    val hours = seconds / PeriodSummaryTO.hours_const_div
//    val s = seconds.rem(PeriodSummaryTO.hours_const_div)
//    val minutes = s / PeriodSummaryTO.min_const_div
//
//    return "${hours}h${minutes.toString().padStart(2, '0')}m"
//}