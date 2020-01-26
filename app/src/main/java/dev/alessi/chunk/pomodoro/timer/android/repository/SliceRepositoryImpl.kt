package dev.alessi.chunk.pomodoro.timer.android.repository

import dev.alessi.chunk.pomodoro.timer.android.database.TaskDao
import dev.alessi.chunk.pomodoro.timer.android.database.TaskSize
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnit
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnitDao
import dev.alessi.chunk.pomodoro.timer.android.util.error

class SliceRepositoryImpl(private val workUnitDao: WorkUnitDao, private val taskDao: TaskDao) :
    SliceRepository {
    private var taskSizeCache: MutableMap<Int, TaskSize>? = null

    override suspend fun loadAllSlicesFromTask(taskId: Int): List<WorkUnit> {

        val wUnits = workUnitDao.selectAllFromTask(taskId, estimation = 0)

        return wUnits.asList()
    }

    override suspend fun loadSlice(workUnitId: Int): WorkUnit {

        val workUnit = workUnitDao.select(workUnitId)

        val task = workUnit.taskId.let { taskDao.load(it) }


        workUnit.task = task
        return workUnit
    }

    override suspend fun storeSlice(workUnit: WorkUnit): Int {
        try {
            return workUnitDao.insertWorkUnit(workUnit.copy(estimation = 0)).toInt()
        } catch (e: Exception) {
            error(e, "erro ao inserir work unit $workUnit")
            throw e
        }

    }




}