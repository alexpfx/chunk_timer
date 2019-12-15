package dev.alessi.chunk.pomodoro.timer.android.repository

import dev.alessi.chunk.pomodoro.timer.android.database.*
import dev.alessi.chunk.pomodoro.timer.android.util.error

class TaskRepositoryImpl(private val taskDao: TaskDao, private val workUnitDao: WorkUnitDao) :
    TaskRepository {
    private var taskSizeCache: MutableMap<Int, TaskSize>? = null

    override suspend fun loadWorkUnit(workUnitId: Int): WorkUnit {
        initCache()
        val workUnit = workUnitDao.loadWorkUnit(workUnitId)
        val taskSize = taskSizeCache?.get(workUnit.sizeId)

        val task = workUnit.taskId.let { taskDao.load(it) }

        workUnit.taskSize = taskSize
        workUnit.task = task
        return workUnit
    }


    override suspend fun allWorkUnitsFromTask(taskId: Int): List<WorkUnit> {
        initCache()

        val wUnits = workUnitDao.loadAllFromTask(taskId)
        for (wu in wUnits) {
            val size = taskSizeCache?.get(wu.sizeId)
            wu.taskSize = size!!

        }

        return wUnits.asList()
    }

    private suspend fun initCache() {
        if (taskSizeCache == null) {
            val sizes = workUnitDao.loadAllSizes()
            taskSizeCache = mutableMapOf()
            taskSizeCache?.let { map ->
                sizes.forEach {
                    map[it.id.toInt()] = it

                }
            }
        }
    }

    override suspend fun updateTask(task: Task): Task {
        taskDao.update(task)

        return task
    }

    override suspend fun loadAllActive(): List<Task> {
        return taskDao.loadAllActive().asList()
    }

    override suspend fun storeTask(task: Task): Task {
        val id = taskDao.insert(task)

        return Task(
            uid = id.toInt(),
            description = task.description,
            dateCreated = task.dateCreated
        )

    }

    override suspend fun storeWorkUnit(workUnit: WorkUnit): Int {
        try {
            return workUnitDao.insertWorkUnit(workUnit).toInt()
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

    override suspend fun loadAllSizes(): List<TaskSize> {
        return workUnitDao.loadAllSizes().asList()
    }

    override suspend fun loadTask(taskId: Int): Task {
        return taskDao.load(taskId)
    }

    override suspend fun allEstimativesFromTask(taskId: Int): List<WorkUnit>{
        return workUnitDao.loadAllEstimativesFromTask(taskId).asList()
    }
}