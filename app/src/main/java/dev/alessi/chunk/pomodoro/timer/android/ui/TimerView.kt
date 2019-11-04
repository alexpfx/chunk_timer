package dev.alessi.chunk.pomodoro.timer.android.ui

import android.content.Intent
import dev.alessi.chunk.pomodoro.timer.android.base.MvpView

interface TimerView : MvpView{

    fun showReloadSizeButtons(sizeMap: Map<Int, Int>)
    fun showTick(intent: Intent)
    fun showTimerStarted()
    fun showBreakTimerStarted()
    fun showBreakTimerCanceled(intent: Intent)
    fun showTimerCanceled(intent: Intent)
    fun showSizeSelected(timeIndex: Int, sizeMap: Map<Int, Int>)
    fun showSizesUpdated(sizeMap: Map<Int, Int>)
    fun showSyncSettings(sizes: Map<Int, Int>)
    fun showBreakTimeChanged(timeMillis: Long)


}