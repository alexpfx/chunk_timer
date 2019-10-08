package dev.alessi.chunk.pomodoro.timer.android

interface TimerView {

    fun showTick(state: Int, timeLeft: Long)
    fun showTimerStarted()
    fun showBreakTimerStarted()
    fun showBreakTimerCanceled()
    fun showTimerCanceled()
    fun showSizeSelected(index: Int)


}