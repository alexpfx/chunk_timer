package dev.alessi.chunk.pomodoro.timer.android

data class TimerState(val currentTime: Long, val totalTime: Long, val status: TimerStatus){
    companion object{
        const val KEY_TOTAL_TIME = "dev.alessi.chunk.pomodoro.timer.android.KEY_TOTAL_TIME"
        const val KEY_CURRENT_TIME = "dev.alessi.chunk.pomodoro.timer.android.KEY_CURRENT_TIME"
        const val KEY_CURRENT_STATUS = "dev.alessi.chunk.pomodoro.timer.android.KEY_CURRENT_STATUS"

    }
}






