package dev.alessi.chunk.pomodoro.timer.android.repository

import dev.alessi.chunk.pomodoro.timer.android.database.TaskSize
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnit

interface SliceRepository {
    suspend fun loadAllSlicesFromTask(taskId: Int): List<WorkUnit>

    suspend fun storeTaskSize(taskSize: TaskSize)
    suspend fun loadAllTaskSizes(): List<TaskSize>
    suspend fun loadSlice(workUnitId: Int): WorkUnit
    suspend fun storeSlice(workUnit: WorkUnit): Int


}