package dev.alessi.chunk.pomodoro.timer.android.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private var _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title


    fun updateTitle(title: String) = _title.postValue(title)

}