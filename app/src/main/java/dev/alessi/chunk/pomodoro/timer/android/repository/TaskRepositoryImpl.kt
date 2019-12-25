package dev.alessi.chunk.pomodoro.timer.android.repository

import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.database.TaskDao

class TaskRepositoryImpl(val taskDao: TaskDao) : TaskRepository {


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

    override suspend fun loadTask(taskId: Int): Task {
        return taskDao.load(taskId)
    }

    override suspend fun updateTask(task: Task): Task {
        taskDao.update(task)

        return task
    }




}