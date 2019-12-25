package dev.alessi.chunk.pomodoro.timer.android.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.alessi.chunk.pomodoro.timer.android.database.Task

class SelectTaskSharedViewModel : ViewModel() {


    private val task = MutableLiveData<Task>()


    fun getTaskObserver() = task

    fun selectTask(task: Task) {
        this.task.value = task
    }

}