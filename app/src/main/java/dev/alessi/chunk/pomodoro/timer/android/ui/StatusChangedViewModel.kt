//package dev.alessi.chunk.pomodoro.timer.android.ui
//
//
//import androidx.lifecycle.*
//import dev.alessi.chunk.pomodoro.timer.android.service.ChunkTimerService
//import dev.alessi.chunk.pomodoro.timer.android.service.ChunkTimerService.TimerState.Companion.status_ready
//import dev.alessi.chunk.pomodoro.timer.android.service.ChunkTimerService.TimerState.Companion.status_running_break
//
//class StatusChangedViewModel : ViewModel() {
//
//
//
//    private val _onTimerStarted = MutableLiveData<Int>()
//
//    private val _onTimerStopped = MutableLiveData<Int>()
//
//    private val _onBreakStarted = MutableLiveData<Int>()
//
//    private val _onTick = MutableLiveData<Int>()
//
//
//    val onTimeStarted: LiveData<Int>
//        get() = _onTimerStarted
//
//    val onBreakStarted: LiveData<Int>
//        get() = _onBreakStarted
//
//    val onTimerStopped: LiveData<Int>
//        get() = _onTimerStopped
//
//    val onTick: LiveData<Int>
//        get () = _onTick
//
//
//
//    fun updateStatus(oldStatus: Int, newStatus: Int) {
//        println("oldStatus: $oldStatus")
//        println("newStatus: $newStatus")
//
//        if (oldStatus == newStatus) {
//            if (newStatus != status_ready){
//                _onTick.value = intent
//            }
//            return
//        }
//
//        if (oldStatus == status_ready) {
//            if (newStatus == status_running_break) {
//                _onBreakStarted.value = intent
//            } else {
//                _onTimerStarted.value = intent
//            }
//
//        } else {
//            _onTimerStopped.value = intent
//        }
//    }
//
//    fun removeAllObservers(lifeCycleOwner: LifecycleOwner) {
//
//        onTimeStarted.removeObservers (lifeCycleOwner)
//        onBreakStarted.removeObservers (lifeCycleOwner)
//        onTimerStopped.removeObservers(lifeCycleOwner)
//
//    }
//
//}