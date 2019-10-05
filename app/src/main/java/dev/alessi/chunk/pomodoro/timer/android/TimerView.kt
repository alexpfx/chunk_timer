package dev.alessi.chunk.pomodoro.timer.android

interface TimerView {

    fun showTick(state: Int, timeLeft: Long, totalTime: Long)
    fun showTimerStarted()
    fun showTimerCanceled()
    fun showSizeSelected(index: Int)


}