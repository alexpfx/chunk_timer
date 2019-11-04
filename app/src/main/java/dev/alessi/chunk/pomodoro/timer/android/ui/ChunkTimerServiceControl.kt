package dev.alessi.chunk.pomodoro.timer.android.ui

import dev.alessi.chunk.pomodoro.timer.android.util.Command


interface ChunkTimerServiceControl{

    fun doStopService()

    fun requestTick()
    fun requestStateUpdate()
    fun doStartService(
        totalTimeMillis: Long,
        index: Int,
        task: String, @Command command: Int
    )
}


