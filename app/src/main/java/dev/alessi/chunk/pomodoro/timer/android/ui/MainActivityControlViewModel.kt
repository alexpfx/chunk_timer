package dev.alessi.chunk.pomodoro.timer.android.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityControlViewModel : ViewModel() {

    private var _title = MutableLiveData<String>()
    private var _subtitle = MutableLiveData<String>()

    val title: LiveData<String>
        get() = _title

    val subtitle: LiveData<String>
        get() = _subtitle


    fun updateTitle(title: String) {
        _title.postValue(title)
        _subtitle.postValue("")
    }

    fun updateSubtitle(subtitle: String) = _subtitle.postValue(subtitle)

}