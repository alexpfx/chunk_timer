package dev.alessi.chunk.pomodoro.timer.android.ui.task

import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.ui.task_stats.PeriodSummaryTO
import dev.alessi.chunk.pomodoro.timer.android.ui.task_stats.PeriodSummaryTO.Period
import dev.alessi.chunk.pomodoro.timer.android.util.debug

data class SelectTaskTO(
    val task: Task,
    val periods: List<PeriodSummaryTO>,

    private var selected: Int = 0,
    var visible: Boolean = true
) {
    companion object{
        var enumPeriods: Array<Period> = enumValues()
    }

    fun rotate(){
        selected ++
        if (selected >= enumPeriods.size){
            selected = 0
        }

        debug("selected: $selected")
    }

    fun getPeriod(): PeriodSummaryTO? {
        val p = enumPeriods[selected]

        return periods.find { it.period == p }
    }

    fun isAllEmpty(): Boolean{
        for (period in periods) {
            if (!period.isEmpty()) {
                return false
            }
        }
        return true
    }






}