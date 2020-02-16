package dev.alessi.chunk.pomodoro.timer.android.repository

import dev.alessi.chunk.pomodoro.timer.android.database.SizeTimeCountTO
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnit

interface EstimationRepository {

    suspend fun storeEstimation(workUnit: WorkUnit): Int

    suspend fun countAllSimilarEstimations(taskId: Int, workUnit: WorkUnit): SizeTimeCountTO
    suspend fun countAllEstimationsFromTask(taskId: Int): List<SizeTimeCountTO>
    suspend fun removeEstimation(workUnit: WorkUnit)
    suspend fun removeAllSimilarEstimations(workUnit: WorkUnit)
}