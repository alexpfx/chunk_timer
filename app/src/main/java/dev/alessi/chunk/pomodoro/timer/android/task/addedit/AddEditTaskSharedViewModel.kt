package dev.alessi.chunk.pomodoro.timer.android.task.addedit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.alessi.chunk.pomodoro.timer.android.App
import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.repository.TaskRepositoryProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddEditTaskSharedViewModel(application: Application) : AndroidViewModel(application) {

    private val addedTask = MutableLiveData<Task>()

    private val scope = CoroutineScope(Dispatchers.Main)

    val onNewTaskObserver
        get() = addedTask as LiveData<Task>


    fun saveTask(task: Task) {
        scope.launch {

            val newTask = withContext(Dispatchers.IO) {
                repository.storeTask(task)
            }

            addedTask.value = newTask
        }
    }


    private val repository
        get() = (getApplication<App>() as TaskRepositoryProvider).taskRepository


}