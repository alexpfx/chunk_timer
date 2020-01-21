package dev.alessi.chunk.pomodoro.timer.android.util

import android.os.Build
import org.threeten.bp.DateTimeUtils
import java.time.temporal.ChronoUnit
import java.util.*

class MyDateUtil {

    companion object {

        fun subDays(date: Date, days: Long): Date {

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Date.from(date.toInstant().plus(days, ChronoUnit.DAYS))
            } else {
                DateTimeUtils.toDate(DateTimeUtils.toInstant(date).plus(days, org.threeten.bp.temporal.ChronoUnit.DAYS))
            }

        }


    }

}