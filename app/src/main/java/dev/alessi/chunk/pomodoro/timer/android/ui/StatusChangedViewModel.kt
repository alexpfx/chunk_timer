package dev.alessi.chunk.pomodoro.timer.android.ui

import android.content.Intent
import androidx.lifecycle.*
import dev.alessi.chunk.pomodoro.timer.android.service.ChunkTimerService
import dev.alessi.chunk.pomodoro.timer.android.service.ChunkTimerService.TimerState.Companion.status_ready
import dev.alessi.chunk.pomodoro.timer.android.service.ChunkTimerService.TimerState.Companion.status_running_break

class StatusChangedViewModel : ViewModel() {




    private val _onTimerStarted = MutableLiveData<Intent>()

    private val _onTimerStopped = MutableLiveData<Intent>()

    private val _onBreakStarted = MutableLiveData<Intent>()

    private val _onTick = MutableLiveData<Intent>()


    val onTimeStarted: LiveData<Intent>
        get() = _onTimerStarted

    val onBreakStarted: LiveData<Intent>
        get() = _onBreakStarted

    val onTimerStopped: LiveData<Intent>
        get() = _onTimerStopped

    val onTick: LiveData<Intent>
        get () = _onTick



    fun updateStatus(intent: Intent) {
        val oldStatus = intent.getIntExtra(ChunkTimerService.extra_param_old_status, 0)
        val newStatus = intent.getIntExtra(ChunkTimerService.extra_param_status, 0)

        println("oldStatus: $oldStatus")
        println("newStatus: $newStatus")


        if (oldStatus == newStatus) {
            if (newStatus != status_ready){
                _onTick.value = intent
            }
            return
        }



        if (oldStatus == status_ready) {
            if (newStatus == status_running_break) {
                _onBreakStarted.value = intent
            } else {
                _onTimerStarted.value = intent
            }

        } else {
            _onTimerStopped.value = intent
        }
    }

    fun removeAllObservers(lifeCycleOwner: LifecycleOwner) {
        onTimeStarted.removeObservers (lifeCycleOwner)
        onBreakStarted.removeObservers (lifeCycleOwner)
        onTimerStopped.removeObservers(lifeCycleOwner)

    }

}