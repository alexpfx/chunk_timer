package dev.alessi.chunk.pomodoro.timer.android.database

import androidx.room.*

@Dao
interface WorkUnitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkUnit(workUnit: WorkUnit): Long

    @Query("select * from WorkUnit where taskId = :taskId and estimative = 0 order by sizeId ")
    suspend fun loadAllFromTask(taskId: Int): Array<WorkUnit>

    @Query("select * from WorkUnit where taskId = :taskId and estimative = 1 order by sizeId ")
    suspend fun loadAllEstimativesFromTask(taskId: Int): Array<WorkUnit>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTaskSize(taskSize: TaskSize): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateTaskSize(taskSize: TaskSize)

    @Query("select * from tasksize where id=:sizeIndex")
    suspend fun loadTaskSize(sizeIndex: Int): TaskSize

    @Query("select * from tasksize")
    suspend fun loadAllSizes(): Array<TaskSize>

    @Query("select * from WorkUnit where uid = :id")
    suspend fun loadWorkUnit(id: Int): WorkUnit

}