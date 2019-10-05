package dev.alessi.chunk.pomodoro.timer.android

interface PomodoroView {

    companion object{
        val EXTRA_TOTAL_TIME = "EXTRATOTALTIME"
    }


    fun showTick(totalTimeInSeconds: Long, secondsToFinish: Long)
    fun showTimerSetted(seconds: Long)
    fun showSetupChanged(tag: Int)
    fun showTimerFinished()
    fun showStatusRunning()
    fun showStatusReady()

    fun persistTimer(remainingTime: Long)
    fun restoreTimer()


}