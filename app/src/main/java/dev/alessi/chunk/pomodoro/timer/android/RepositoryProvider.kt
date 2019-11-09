package dev.alessi.chunk.pomodoro.timer.android

import dev.alessi.chunk.pomodoro.timer.android.repository.TaskRepository

interface RepositoryProvider {
    fun getTaskRepository() : TaskRepository

}