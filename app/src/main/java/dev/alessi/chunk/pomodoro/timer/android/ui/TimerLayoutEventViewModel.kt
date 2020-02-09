package dev.alessi.chunk.pomodoro.timer.android.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerLayoutEventViewModel : ViewModel() {
    private val _event = MutableLiveData<LayoutEvent>()

    val onEventFiredObserver: LiveData<LayoutEvent>
        get() = _event


    fun fireEvent(eventType: Int, newStatus: TimerFragmentViewState) {
        _event.value = LayoutEvent(eventType, newStatus)
    }


    data class TimerFragmentViewState(
        val controlId: Int = 1,
        val showGroupTask: Boolean = false,
        val showStartTimerButton: Boolean = true,
        val showTxtTaskEmpty: Boolean = true,
        val isTimer: Boolean = true,
        val txtTimerTitleRes: Int = 0,
        val readOnly: Boolean = false,
        val chunkIndex: Int = 2,
        val breaktimeIndex: Int = 0,
        var chunkSizes: List<Int> = listOf(12, 24, 36, 48, 60),
        var breaktimeSizes: List<Int> = listOf(5, 10, 20)


    ) {
        fun copyAndIncId(): TimerFragmentViewState {
            return copy(controlId = controlId + 1)
        }


    }

    data class LayoutEvent(@TimerLayoutEventType val eventType: Int, val newStatus: TimerFragmentViewState)


    @Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION, AnnotationTarget.LOCAL_VARIABLE)
    annotation class TimerLayoutEventType {
        companion object {
            const val TIMER_PAGE_SELECTED = 0
            const val BREAKTIME_PAGE_SELECTED = 1
            const val TIMER_STARTED = 2
            const val TIMER_STOPPED = 3
            const val TASK_SELECTED = 4
            const val TASK_CLEARED = 5
            const val CLOCK_TIMER_SETUP = 6
            const val INDEX_CHANGE = 7
        }
    }

}