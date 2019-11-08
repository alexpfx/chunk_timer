package dev.alessi.chunk.pomodoro.timer.android.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface WorkUnitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkUnit(workUnit: WorkUnit)




}