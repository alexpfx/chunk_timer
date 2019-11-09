package dev.alessi.chunk.pomodoro.timer.android.repository

import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.database.TaskDao
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnitDao

class TaskRepositoryImpl (private val taskDao: TaskDao, val workUnitDao: WorkUnitDao) : TaskRepository {
    override suspend fun updateTask(task: Task) : Task {
        taskDao.update(task)

        return task
    }

    override suspend fun loadAllTasks(): Array<Task> {
        return taskDao.loadAll()
    }

    override suspend fun storeTask(task: Task): Task {
        val id = taskDao.insert(task)

        return Task(uid = id.toInt(), description = task.description)

    }

    override suspend fun loadTask(taskId: Int) {

        taskDao.load(taskId)


    }
}