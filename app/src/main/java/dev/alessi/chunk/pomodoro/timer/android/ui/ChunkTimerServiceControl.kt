package dev.alessi.chunk.pomodoro.timer.android.ui

import dev.alessi.chunk.pomodoro.timer.android.util.Command


interface ChunkTimerServiceControl {

    fun doStopService()


    fun requestStateUpdate()
    fun doStartService(
        totalTimeMillis: Long,
        index: Int,
        taskId: Int,
        @Command command: Int
    )


}


