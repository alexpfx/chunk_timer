package dev.alessi.chunk.pomodoro.timer.android.util

import android.os.Build
import dev.alessi.chunk.pomodoro.timer.android.ui.slice_history.adapter.DataItem
import dev.alessi.chunk.pomodoro.timer.android.util.date.DateUtil
import dev.alessi.chunk.pomodoro.timer.android.util.date.NewTimeApiDateUtil
import dev.alessi.chunk.pomodoro.timer.android.util.date.ThreetenDateUtil
import java.util.*

class MyDateUtil internal constructor(private val dateUtil: DateUtil) : DateUtil {


    companion object {
        fun getInstance(): MyDateUtil {
            return if (Build.VERSION.SDK_INT < 26) {
                MyDateUtil(ThreetenDateUtil())
            } else {
                MyDateUtil(NewTimeApiDateUtil())
            }
        }
    }


    override fun startOfDay(baseDate: Date): Date {
        return dateUtil.startOfDay(baseDate)
    }

    override fun startOfWeek(baseDate: Date): Date {
        return dateUtil.startOfWeek(baseDate)
    }

    override fun startOfMonth(baseDate: Date): Date {
        return dateUtil.startOfMonth(baseDate)
    }

    override fun addDaysTo(date: Date, days: Long): Date {
        return dateUtil.addDaysTo(date, days)
    }


}

