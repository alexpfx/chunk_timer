package dev.alessi.chunk.pomodoro.timer.android.repository

interface RepositoryProvider<T> {
    val repository: T
}