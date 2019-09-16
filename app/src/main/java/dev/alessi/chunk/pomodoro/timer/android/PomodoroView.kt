package dev.alessi.chunk.pomodoro.timer.android

interface PomodoroView {

    fun showTick(totalTimeInSeconds: Long, secondsToFinish: Long)

    fun showTimerSetted(seconds: Long)

    fun showSetupChanged(tag: Int)

    fun showTimerCanceled()

    fun showTimerFinished()

    fun showTimerStarted()

    fun showStateChanged(newStatus: TimerStatus)


}