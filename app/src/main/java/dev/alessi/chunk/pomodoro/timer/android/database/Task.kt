package dev.alessi.chunk.pomodoro.timer.android.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    @PrimaryKey
    val uid: Int,
    val description: String

)
