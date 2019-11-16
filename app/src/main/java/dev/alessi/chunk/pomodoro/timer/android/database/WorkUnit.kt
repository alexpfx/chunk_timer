package dev.alessi.chunk.pomodoro.timer.android.database

import androidx.room.*
import java.util.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = Task::class,
        parentColumns = arrayOf("uid"),
        childColumns = arrayOf("taskId")
    ), ForeignKey(entity = TaskSize::class, parentColumns = ["id"], childColumns = ["sizeId"])]
)
data class WorkUnit(
    @PrimaryKey(autoGenerate = true)
    var uid: Int? = null,
    var finishDate: Date,
    var timeMinutes: Int,
    var taskId: Int,
    var sizeId: Int,
    @Ignore
    var task: Task? = null,
    @Ignore
    var taskSize: TaskSize? = null




){
    constructor(): this(0, Date(), 0,0, 0,null, null)
}