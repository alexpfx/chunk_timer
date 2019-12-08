package dev.alessi.chunk.pomodoro.timer.android.database

import androidx.room.*

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Query("select * from task order by dateCreated desc")
    suspend fun loadAll(): Array<Task>

    @Query("select * from task where archived = 0 order by dateCreated desc")
    suspend fun loadAllActive(): Array<Task>

    @Query("select * from task where uid = :id")
    suspend fun load(id: Int): Task

}