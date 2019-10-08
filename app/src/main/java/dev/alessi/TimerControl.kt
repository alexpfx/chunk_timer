package dev.alessi

import android.content.Intent
import dev.alessi.chunk.pomodoro.timer.android.TimerView

interface TimerControl {

    fun onBound(view: TimerView)
    fun onUnBound()
    fun doStartService(intent: Intent)
    fun doStopService(intent: Intent)


    fun onSetup(totalTime: Long)
    fun startTimer()
    fun startBreak()
    fun cancelTimer()
    fun cancelBreak()
    fun onFinishTimer()
    fun onFinishBreak()


}