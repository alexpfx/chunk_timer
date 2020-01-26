package dev.alessi.chunk.pomodoro.timer.android.util.date

import java.util.*

interface DateUtil {
    fun startOfDay(baseDate: Date): Date
    fun startOfWeek(baseDate: Date): Date
    fun startOfMonth(baseDate: Date): Date
    fun addDaysTo(date: Date, days: Long): Date
}