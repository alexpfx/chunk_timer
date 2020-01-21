package dev.alessi.chunk.pomodoro.timer.android.ui.task_stats

import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnit
import dev.alessi.chunk.pomodoro.timer.android.domain.SizeIndex
import dev.alessi.chunk.pomodoro.timer.android.util.toFormatedTime
import java.text.DecimalFormat
import java.util.*


data class PeriodSummaryTO(
    val period: Period,
    val minutes: Int,
    val sizeMap: MutableMap<Int, Int>
) {


    companion object {
        const val hours_const_div = 3600
        const val min_const_div = 60


        fun from(slices: List<WorkUnit>): PeriodSummaryTO {
            var minuteCount = 0
            for (slice in slices) {
                minuteCount += slice.timeMinutes
            }

            return PeriodSummaryTO(Period.ALL, minuteCount, mutableMapOf())
        }

        fun from(name: Period, slices: List<WorkUnit>): PeriodSummaryTO {
            var minutes = 0

            val sizeMap = mutableMapOf<Int, Int>()
            IntRange(SizeIndex.PP, SizeIndex.GG).forEach { sizeMap[it] = 0 }


            val sortedSlices = slices.sortedBy { it.sizeId }
            for (slice in sortedSlices) {
                minutes += slice.timeMinutes
                val x = sizeMap[slice.sizeId]?.plus(1)!!
                sizeMap[slice.sizeId] = x

            }

            return PeriodSummaryTO(name, minutes, sizeMap)
        }
    }

    fun isEmpty() = minutes == 0

    fun toFormatedTime(): String {
        return minutes.toFormatedTime()
    }


    fun toFormatedHours(): String {
        val seconds = minutes * 60
        val hours: Double = seconds / hours_const_div.toDouble()
        return DecimalFormat("#.#").format(hours)
    }


    data class TimePeriod(val name: String, val filter: (WorkUnit) -> Boolean) {
        override fun toString(): String {
            return name
        }


    }


    data class Interval(val start: Date, val end: Date)


    enum class Period(val labelKey: Int, val dateInterval: () -> Interval) {

        TODAY(R.string.label_period_today, {
            val now = Date()
            Interval(
                start = now,
                end = dev.alessi.chunk.pomodoro.timer.android.util.MyDateUtil.subDays(now, 7)
            )
        }),

        THIS_WEEK(R.string.label_period_this_week, {
            val now = Date()
            Interval(
                start = now,
                end = dev.alessi.chunk.pomodoro.timer.android.util.MyDateUtil.subDays(now, 7)
            )
        }),
        LAST_X_DAYS(R.string.label_period_last_7_days, {
            val now = Date()
            Interval(
                start = now,
                end = dev.alessi.chunk.pomodoro.timer.android.util.MyDateUtil.subDays(now, 7)
            )
        }),
        THIS_MONTH(R.string.label_period_this_month, {
            val now = Date()
            Interval(
                start = now,
                end = dev.alessi.chunk.pomodoro.timer.android.util.MyDateUtil.subDays(now, 7)
            )
        }),
        ALL(R.string.label_period_all, {
            val now = Date()
            Interval(
                start = now,
                end = dev.alessi.chunk.pomodoro.timer.android.util.MyDateUtil.subDays(now, 7)
            )
        });

        override fun toString(): String {
            return "Period(labelKey=$labelKey)"
        }


    }

}