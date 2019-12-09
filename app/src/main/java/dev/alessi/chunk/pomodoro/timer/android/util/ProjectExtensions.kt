package dev.alessi.chunk.pomodoro.timer.android.util

import android.util.Log
import java.util.*

fun Any.debug(message: String) {
    Log.d(this.javaClass.name, message)
}

fun Number.bool() = this != 0


fun Date.beginningOfDay(): Date {
    return createCalendarBeginningOfDay().time
}

fun Date.beginningOfWeek(): Date {
    val cal = createCalendarBeginningOfDay()
    cal.set(Calendar.DAY_OF_YEAR, cal.firstDayOfWeek)
    return cal.time
}

fun Date.beginningOfMonth(): Date {
    val cal = createCalendarBeginningOfDay()
    cal.set(Calendar.DAY_OF_MONTH, 1)
    return cal.time
}

private fun createCalendarBeginningOfDay(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.clear(Calendar.MINUTE)
    calendar.clear(Calendar.SECOND)
    calendar.clear(Calendar.MILLISECOND)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    return calendar
}