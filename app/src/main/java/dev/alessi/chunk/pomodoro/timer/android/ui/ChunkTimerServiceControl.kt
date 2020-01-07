package dev.alessi.chunk.pomodoro.timer.android.ui

import dev.alessi.chunk.pomodoro.timer.android.service.ChunkTimerService



interface ChunkTimerServiceControl {

    fun doStopService()

    fun requestStateUpdate()
    fun doStartService(
        totalTimeMillis: Long,
        index: Int,
        taskId: Int,
        @ChunkTimerService.Command command: Int
    )


}


