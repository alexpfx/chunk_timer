package dev.alessi.chunk.pomodoro.timer.android.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.alessi.chunk.pomodoro.timer.android.database.Task

class TimerSharedViewModel : ViewModel() {

    val breaktime = MutableLiveData<Int>()
    val sizeIndex = MutableLiveData<Int>()
    val sizes = MutableLiveData<List<Int>>()

    val task = MutableLiveData<Task>()


    fun setBreaktime(breaktimeMinutes: Int) {
        this.breaktime.value = breaktimeMinutes
    }


    fun setSizeIndex(sizeIndex: Int) {
        this.sizeIndex.value = sizeIndex
    }


    fun setSizes(sizes: List<Int>) {
        this.sizes.value = sizes
    }

}