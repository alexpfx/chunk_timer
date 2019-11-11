package dev.alessi.chunk.pomodoro.timer.android.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "timeSlice",
    foreignKeys = [ForeignKey(
        entity = Task::class,
        parentColumns = arrayOf("uid"),
        childColumns = arrayOf("taskId")
    )]
)
data class WorkUnit(
    @PrimaryKey(autoGenerate = true)
    val uid: Int? = null,
    val finishDate: Date,
    val size: Int,
    val timeMinutes: Int,
    val taskId: Int
)