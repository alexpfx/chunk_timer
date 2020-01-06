package dev.alessi.chunk.pomodoro.timer.android.util

import android.content.Context
import android.util.Log
import android.util.TypedValue
import dev.alessi.chunk.pomodoro.timer.android.database.SizeTimeCountTO
import dev.alessi.chunk.pomodoro.timer.android.domain.SizeValue
import dev.alessi.chunk.pomodoro.timer.android.ui.task_stats.PeriodSummaryTO
import java.util.*

fun Any.debug(message: String) {
    Log.d(this.javaClass.name, message)
}

fun Any.error(err: Throwable, message: String = "") {
    Log.e(this.javaClass.name, message, err)
}

fun Number.bool() = this != 0

fun Number.toDip(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        toFloat(),
        context.resources.displayMetrics
    ).toInt()
}



fun Int.minutesToDegree(): Float{
    return this * 6f
}
fun Int.hoursToDegree(): Float{
    return this * 30f
}

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


fun Long.toMinutes(): Long{
    return (this / 60000)

}

fun Int.toFormatedTime(): String {
    val seconds = this * 60
    val hours = seconds / PeriodSummaryTO.hours_const_div
    val s = seconds.rem(PeriodSummaryTO.hours_const_div)
    val minutes = s / PeriodSummaryTO.min_const_div

    return "${hours}h${minutes.toString().padStart(2, '0')}m"
}

fun SizeTimeCountTO.toSizeValue(): SizeValue {
    return SizeValue(this.sizeId, this.timeMinutes)
}

fun SizeTimeCountTO.toFormatedSummary(count: Int, countPad: Int = 2, minutesPad: Int = 2): String {
    return "${count.toString().padStart(3, ' ')} x ${this.timeMinutes.toString().padStart(
        minutesPad,
        ' '
    )}"
}

private fun createCalendarBeginningOfDay(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.clear(Calendar.MINUTE)
    calendar.clear(Calendar.SECOND)
    calendar.clear(Calendar.MILLISECOND)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    return calendar
}