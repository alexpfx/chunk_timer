package dev.alessi.chunk.pomodoro.timer.android.ui.task

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.alessi.chunk.pomodoro.timer.android.App
import dev.alessi.chunk.pomodoro.timer.android.RepositoryProvider
import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskViewModel(application: Application) : AndroidViewModel(application) {


    private val scope = CoroutineScope(Dispatchers.Main)

    private val archivedTask = MutableLiveData<Task>()

    val archiveActionObserver
        get() = archivedTask as LiveData<Task>


    fun archiveTask(task: Task) {
        scope.launch {
            val updatedTask = withContext(Dispatchers.IO) {
                getRepository().updateTask(task.copy(archived = 1))
            }

            archivedTask.value = updatedTask

        }
    }

    fun unarchiveTask(task: Task) {
        scope.launch {
            val updatedTask = withContext(Dispatchers.IO) {
                getRepository().updateTask(task.copy(archived = 0))
            }
            archivedTask.value = updatedTask
        }


    }


    private fun getRepository(): TaskRepository {
        return (getApplication<App>() as RepositoryProvider).getTaskRepository()
    }


}