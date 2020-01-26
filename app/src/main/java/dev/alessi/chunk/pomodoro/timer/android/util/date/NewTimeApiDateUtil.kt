package dev.alessi.chunk.pomodoro.timer.android.util.date

import android.annotation.TargetApi
import android.os.Build
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoField
import java.util.*

@TargetApi(Build.VERSION_CODES.O)
class NewTimeApiDateUtil : DateUtil {

    override fun startOfDay(baseDate: Date): Date {
        val zdt: ZonedDateTime = baseDate.toInstant().atZone(ZoneId.systemDefault())

        return Date.from(zdt.with(ChronoField.NANO_OF_DAY, 0).toInstant())
    }

    override fun startOfWeek(baseDate: Date): Date {
        val startOfTheWeek = dateFrom(baseDate, ChronoField.DAY_OF_WEEK, 1)
        return startOfDay(startOfTheWeek)
    }

    override fun startOfMonth(baseDate: Date): Date {
        val startOfMonth = dateFrom(baseDate, ChronoField.DAY_OF_MONTH, 1)
        return startOfDay(startOfMonth)
    }

    private fun dateFrom(date: Date, chronoField: ChronoField, value: Long): Date {
        val zdt: ZonedDateTime = date.toInstant().atZone(ZoneId.systemDefault())
        return Date.from(zdt.with(chronoField, value).toInstant())

    }

    override fun addDaysTo(baseDate: Date, days: Long): Date {
        val zdt: ZonedDateTime = baseDate.toInstant().atZone(ZoneId.systemDefault())

        return Date.from(zdt.plusDays(days).toInstant())
    }
}