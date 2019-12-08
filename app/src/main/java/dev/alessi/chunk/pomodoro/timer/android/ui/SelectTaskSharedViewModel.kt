package dev.alessi.chunk.pomodoro.timer.android.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.alessi.chunk.pomodoro.timer.android.database.Task

class SelectTaskSharedViewModel: ViewModel() {

    private val taskname = MutableLiveData<String>()

    private val task = MutableLiveData<Task>()

    fun getTaskname() = taskname


    fun getTaskObserver() = task

    fun selectTask(task: Task){
        this.task.value = task
    }

}