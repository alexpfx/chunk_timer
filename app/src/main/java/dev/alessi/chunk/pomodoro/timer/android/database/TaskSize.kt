package dev.alessi.chunk.pomodoro.timer.android.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TaskSize (
    @PrimaryKey(autoGenerate = false)
    val id: Long,

    val name: String
)



