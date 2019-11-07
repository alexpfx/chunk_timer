package dev.alessi.chunk.pomodoro.timer.android.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    val breaktime = MutableLiveData<Int>()
    val sizeIndex = MutableLiveData<Int>()
    val sizes = MutableLiveData<List<Int>>()
    val taskname = MutableLiveData<String>()



    fun setBreaktime(breaktimeMinutes: Int) {
        this.breaktime.value = breaktimeMinutes
    }


    fun setSizeIndex(sizeIndex: Int) {
        this.sizeIndex.value = sizeIndex
    }


    fun setSizes(sizes: List<Int>) {
        this.sizes.value = sizes
    }

    fun setTaskname (task: String){
        this.taskname.value = task
    }


    override fun onCleared() {
        super.onCleared()
    }

}