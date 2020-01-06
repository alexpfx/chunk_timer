package dev.alessi.chunk.pomodoro.timer.android.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.alessi.chunk.pomodoro.timer.android.database.Task

class TimerSharedViewModel : ViewModel() {

    val breaktime = MutableLiveData<Int>()
    private val _sizeIndex = MutableLiveData<Int>()


    val sizes = MutableLiveData<List<Int>>()

    val task = MutableLiveData<Task>()


    fun setBreaktime(breaktimeMinutes: Int) {
        this.breaktime.value = breaktimeMinutes
    }

    val sizeIndex: LiveData<Int>
        get() = _sizeIndex


    fun setSizeIndex(sizeIndex: Int) {
        this._sizeIndex.value = sizeIndex
    }


    fun setSizes(sizes: List<Int>) {
        this.sizes.value = sizes
    }

}