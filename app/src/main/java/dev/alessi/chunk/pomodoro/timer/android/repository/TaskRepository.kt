package dev.alessi.chunk.pomodoro.timer.android.repository

import dev.alessi.chunk.pomodoro.timer.android.database.Task

interface TaskRepository {

    suspend fun storeTask(task: Task): Task
    suspend fun loadTask(taskId: Int)
    suspend fun loadAllTasks(): Array<Task>
    suspend fun updateTask(task: Task): Task

}