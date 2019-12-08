package dev.alessi.chunk.pomodoro.timer.android.repository

import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.database.TaskSize
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnit

interface TaskRepository {

    suspend fun storeTask(task: Task): Task
    suspend fun loadTask(taskId: Int): Task
    suspend fun loadWorkUnit(workUnitId: Int): WorkUnit
    suspend fun loadAllActive(): Array<Task>
    suspend fun loadAllFromTask(taskId: Int): Array<WorkUnit>
    suspend fun updateTask(task: Task): Task

    suspend fun storeWorkUnit(workUnit: WorkUnit): Int

    suspend fun storeTaskSize(taskSize: TaskSize)
}