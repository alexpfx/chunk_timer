package dev.alessi.chunk.pomodoro.timer.android.database

import androidx.room.*

@Dao
interface WorkUnitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkUnit(workUnit: WorkUnit): Long

    @Query("select * from WorkUnit where taskId = :taskId and estimative = :estimative order by sizeId ")
    suspend fun selectAllFromTask(taskId: Int, estimative: Int): Array<WorkUnit>


    @Query("select sizeId, timeMinutes, count(uid) as count from WorkUnit where taskId = :taskId and estimative = :estimative and sizeId = :sizeId and timeMinutes = :timeMinutes group by timeMinutes order by timeMinutes")
    suspend fun countAllSimilar(
        taskId: Int,
        sizeId: Int,
        timeMinutes: Int,
        estimative: Int
    ): SizeTimeCountTO


    @Query("select sizeId, timeMinutes, count(uid) as count from WorkUnit where taskId = :taskId and estimative = :estimative group by sizeId, timeMinutes order by sizeId, timeMinutes")
    suspend fun countAllFromTask(taskId: Int, estimative: Int): List<SizeTimeCountTO>


    @Query("delete from workunit where uid in (select min(uid) from workunit where taskId = :taskId and sizeId =:sizeId and timeMinutes =:timeMinutes and estimative = :estimative)")
    suspend fun deleteFirstSimilar(taskId: Int, sizeId: Int, timeMinutes: Int, estimative: Int)


    @Query("delete from workunit where taskId = :taskId and sizeId = :sizeId and timeMinutes = :timeMinutes and estimative = :estimative")
    suspend fun deleteAllSimilar(
        taskId: Int,
        sizeId: Int,
        timeMinutes: Int,
        estimative: Int
    )

    @Query("select * from WorkUnit where uid = :id")
    suspend fun select(id: Int): WorkUnit


    @Query("select * from tasksize where id=:sizeIndex")
    suspend fun loadTaskSize(sizeIndex: Int): TaskSize

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTaskSize(taskSize: TaskSize): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateTaskSize(taskSize: TaskSize)

    @Query("select * from tasksize")
    suspend fun selectAllTaskSizes(): Array<TaskSize>




}