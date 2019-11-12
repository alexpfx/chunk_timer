package dev.alessi.chunk.pomodoro.timer.android.repository

import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.database.TaskDao
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnit
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnitDao

class TaskRepositoryImpl (private val taskDao: TaskDao, val workUnitDao: WorkUnitDao) : TaskRepository {
    override suspend fun loadAllFromTask(taskId: Int): Array<WorkUnit> {
        return workUnitDao.loadAllFromTask(taskId)
    }

    override suspend fun updateTask(task: Task) : Task {
        taskDao.update(task)

        return task
    }

    override suspend fun loadAllTasks(): Array<Task> {
        return taskDao.loadAll()
    }

    override suspend fun storeTask(task: Task): Task {
        val id = taskDao.insert(task)

        return Task(uid = id.toInt(), description = task.description, dateCreated = task.dateCreated)

    }

    override suspend fun storeWorkUnit(workUnit: WorkUnit){
        val id = workUnitDao.insertWorkUnit(workUnit)
    }

    override suspend fun loadTask(taskId: Int): Task {

        return taskDao.load(taskId)


    }
}