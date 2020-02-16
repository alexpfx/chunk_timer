package dev.alessi.chunk.pomodoro.timer.android.repository

import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnit

interface SliceRepository {
    suspend fun loadAllSlicesFromTask(taskId: Int): List<WorkUnit>



    suspend fun loadSlice(workUnitId: Int): WorkUnit
    suspend fun storeSlice(workUnit: WorkUnit): Int


}