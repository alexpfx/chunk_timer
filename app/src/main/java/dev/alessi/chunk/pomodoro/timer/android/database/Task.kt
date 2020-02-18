package dev.alessi.chunk.pomodoro.timer.android.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.alessi.chunk.pomodoro.timer.android.util.bool
import java.util.*

@Entity(ignoredColumns = ["slices"])
data class Task(
    @PrimaryKey(autoGenerate = true)
    var uid: Int?,
    var name: String = "",
    var description: String = "",
    var dateCreated: Date? = null,
    var slices: List<WorkUnit> = listOf(),
    var archived: Int = 0,
    var markedAsDone: Int = 0


) {
    constructor() : this(null, "", "", null, listOf())

    fun status(): Int {
        return if (archived.bool()) {
            return Status.IS_ARCHIVED
        } else if (markedAsDone.bool()) {
            return Status.IS_COMPLETED
        } else if (slices.filter { !it.estimation.bool() }.isEmpty()) {
            return Status.IS_STARTED
        } else {
            return Status.IS_NEW
        }
    }


    @Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION, AnnotationTarget.LOCAL_VARIABLE)
    annotation class Status {
        companion object {
            const val IS_ARCHIVED = 0
            const val IS_NEW = 1
            const val IS_STARTED = 2
            const val IS_COMPLETED = 3
        }
    }


}


