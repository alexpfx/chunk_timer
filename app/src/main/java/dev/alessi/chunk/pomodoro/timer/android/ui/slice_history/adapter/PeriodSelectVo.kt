package dev.alessi.chunk.pomodoro.timer.android.ui.slice_history.adapter


data class PeriodSelectVo (val name: String, val id: Int, val filter: IntervalFilter){

    override fun toString(): String {
        return name
    }

}



