package dev.alessi.chunk.pomodoro.timer.android.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.alessi.chunk.pomodoro.timer.android.database.Task

class TaskSharedViewModel: ViewModel() {

    private val taskname = MutableLiveData<String>()

    private val task = MutableLiveData<Task>()

    fun getTaskname() = taskname

    fun setTaskname (task: String){
        this.taskname.value = task
    }

    fun getTask() = task

    fun setTask(task: Task){
        this.task.value = task
    }

}