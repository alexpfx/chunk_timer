package dev.alessi.chunk.pomodoro.timer.android.ui.task_stats

import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.database.SizeIndex
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnit


data class PeriodSummaryTO(
    val period: Period,
    val minutes: Int,
    val sizeMap: MutableMap<Int, Int>

) {

    companion object {
        const val hours_const_div = 3600
        const val min_const_div = 60


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

    fun isEmpty () = minutes == 0

    fun toFormatedTime(): String {
        val seconds = minutes * 60
        val hours = seconds / hours_const_div
        val s = seconds.rem(hours_const_div)
        val minutes = s / min_const_div

        return "${hours}h ${minutes.toString().padStart(2, '0')}m"
    }


    enum class Period(val labelKey: Int) {
        TODAY(R.string.label_today),
        THIS_WEEK(R.string.label_this_week),
        THIS_MONTH(R.string.label_this_month)
    }

}