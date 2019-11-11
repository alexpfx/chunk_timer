package dev.alessi.chunk.pomodoro.timer.android.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WorkUnitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkUnit(workUnit: WorkUnit)

    @Query("select uid, finishDate, size, taskId, timeMinutes from timeSlice where taskId = :taskId order by size ")
    suspend fun loadAllFromTask(taskId: Int): Array<WorkUnit>

}