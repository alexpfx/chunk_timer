package dev.alessi.chunk.pomodoro.timer.android.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimeSizeViewModel : ViewModel() {

    private val sizes = MutableLiveData<List<Int>>()


    fun getSizes() = sizes

    fun setSizes(sizes: List<Int>) {
        this.sizes.value = sizes
    }


}