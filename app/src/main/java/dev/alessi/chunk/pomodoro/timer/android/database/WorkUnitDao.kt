package dev.alessi.chunk.pomodoro.timer.android.database

import androidx.room.*

@Dao
interface WorkUnitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkUnit(workUnit: WorkUnit): Long

    @Query("select * from WorkUnit where taskId = :taskId and estimation = :estimation order by sizeId ")
    suspend fun selectAllFromTask(taskId: Int, estimation: Int): Array<WorkUnit>


    @Query("select sizeId, timeMinutes, count(uid) as count from WorkUnit where taskId = :taskId and estimation = :estimation and sizeId = :sizeId and timeMinutes = :timeMinutes group by timeMinutes order by timeMinutes")
    suspend fun countAllSimilar(
        taskId: Int,
        sizeId: Int,
        timeMinutes: Int,
        estimation: Int
    ): SizeTimeCountTO


    @Query("select sizeId, timeMinutes, count(uid) as count from WorkUnit where taskId = :taskId and estimation = :estimation group by sizeId, timeMinutes order by sizeId, timeMinutes")
    suspend fun countAllFromTask(taskId: Int, estimation: Int): List<SizeTimeCountTO>


    @Query("delete from workunit where uid in (select min(uid) from workunit where taskId = :taskId and sizeId =:sizeId and timeMinutes =:timeMinutes and estimation = :estimation)")
    suspend fun deleteFirstSimilar(taskId: Int, sizeId: Int, timeMinutes: Int, estimation: Int)


    @Query("delete from workunit where taskId = :taskId and sizeId = :sizeId and timeMinutes = :timeMinutes and estimation = :estimation")
    suspend fun deleteAllSimilar(
        taskId: Int,
        sizeId: Int,
        timeMinutes: Int,
        estimation: Int
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