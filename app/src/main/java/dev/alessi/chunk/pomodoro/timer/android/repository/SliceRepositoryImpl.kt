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
        initCache()

        val wUnits = workUnitDao.selectAllFromTask(taskId, estimative = 0)
        for (wu in wUnits) {
            val size = taskSizeCache?.get(wu.sizeId)
            wu.taskSize = size!!

        }

        return wUnits.asList()
    }

    override suspend fun loadSlice(workUnitId: Int): WorkUnit {
        initCache()
        val workUnit = workUnitDao.select(workUnitId)
        val taskSize = taskSizeCache?.get(workUnit.sizeId)

        val task = workUnit.taskId.let { taskDao.load(it) }

        workUnit.taskSize = taskSize
        workUnit.task = task
        return workUnit
    }

    override suspend fun storeSlice(workUnit: WorkUnit): Int {
        try {
            return workUnitDao.insertWorkUnit(workUnit.copy(estimative = 0)).toInt()
        } catch (e: Exception) {
            error(e, "erro ao inserir work unit $workUnit")
            throw e
        }

    }


    override suspend fun storeTaskSize(taskSize: TaskSize) {
        val id = workUnitDao.insertTaskSize(taskSize)
        if (id == -1L) {
            workUnitDao.updateTaskSize(taskSize)
        }
    }

    override suspend fun loadAllTaskSizes(): List<TaskSize> {
        return workUnitDao.selectAllTaskSizes().asList()
    }

    private suspend fun initCache() {
        if (taskSizeCache == null) {
            val sizes = workUnitDao.selectAllTaskSizes()
            taskSizeCache = mutableMapOf()
            taskSizeCache?.let { map ->
                sizes.forEach {
                    map[it.id.toInt()] = it

                }
            }
        }
    }


}