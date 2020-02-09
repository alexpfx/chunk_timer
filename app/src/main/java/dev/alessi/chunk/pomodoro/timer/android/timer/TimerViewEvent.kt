package dev.alessi.chunk.pomodoro.timer.android.timer

sealed class TimerViewEvent {
    object StartTimer: TimerViewEvent()
    object SelectTask: TimerViewEvent()
    object CancelTimer: TimerViewEvent()
    object SelectTimer: TimerViewEvent()
    object ChangeTimerType: TimerViewEvent()
    object OpenTimersConfig: TimerViewEvent()
    object OpenBreaktimersConfig: TimerViewEvent()

}