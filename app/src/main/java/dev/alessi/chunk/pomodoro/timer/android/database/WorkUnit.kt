package dev.alessi.chunk.pomodoro.timer.android.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = Task::class,
        parentColumns = arrayOf("uid"),
        childColumns = arrayOf("taskId")
    )]
)
data class WorkUnit(
    @PrimaryKey(autoGenerate = true)
    var uid: Int? = null,
    var finishDate: Date = Date(),
    var timeMinutes: Int,
    var taskId: Int,
    var sizeId: Int,
    var estimation: Int = 0,

    @Ignore
    var task: Task? = null


) {


    constructor() : this(0, Date(), 0, 0, 0, 0, null)

    override fun toString(): String {
        return "WorkUnit(uid=$uid, finishDate=$finishDate, timeMinutes=$timeMinutes, taskId=$taskId, sizeId=$sizeId, estimation=$estimation, task=$task)"
    }


}