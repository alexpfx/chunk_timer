package dev.alessi.chunk.pomodoro.timer.android.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConfigTimersSharedViewModel : ViewModel() {

    private val _sizes = MutableLiveData<List<Int>>()

    private val _breakTimes = MutableLiveData<List<Int>>()

    val onChunkSizesChangedObserver: LiveData<List<Int>>
        get() = _sizes

    val onBreaktimesChangedObserver: LiveData<List<Int>>
        get() = _breakTimes


    fun setBreaktimes(sizes: List<Int>){
        this._breakTimes.value = sizes
    }


    fun setSizes(sizes: List<Int>) {
        this._sizes.value = sizes
    }



}