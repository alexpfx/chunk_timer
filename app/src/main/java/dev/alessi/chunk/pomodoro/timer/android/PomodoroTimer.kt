package dev.alessi.chunk.pomodoro.timer.android

import android.os.CountDownTimer

class PomodoroTimer(
    totalTimeInMinutes: Long,
    tickTimeInSeconds: Long,
    private val onTickCallback: (secondsToFinish: Long) -> Unit,
    val onFinishCallback: () -> Unit
) :
    CountDownTimer((totalTimeInMinutes * 60 * 1000L), tickTimeInSeconds * 1000) {


    override fun onFinish() {
        onFinishCallback()
    }

    override fun onTick(millisToFinish: Long) {
        this.onTickCallback(millisToFinish / 1000)
    }


}