package dev.alessi.chunk.pomodoro.timer.android.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(ignoredColumns = ["slices"])
data class Task(
    @PrimaryKey(autoGenerate = true)
    var uid: Int?,
    var description: String = "",
    var dateCreated: Date? = null,
    var slices: List<WorkUnit> = listOf()



){
    constructor(): this(null, "", null, listOf())
}
