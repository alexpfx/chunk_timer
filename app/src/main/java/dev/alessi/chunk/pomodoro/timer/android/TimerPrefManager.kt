package dev.alessi.chunk.pomodoro.timer.android

import android.content.SharedPreferences


class TimerPrefManager(val sharedPreferences: SharedPreferences) {


    fun saveTimer(timer: TimerState) {
        val edit = sharedPreferences.edit()
        edit.putLong(TimerState.KEY_CURRENT_TIME, timer.currentTime)
        edit.putLong(TimerState.KEY_TOTAL_TIME, timer.totalTime)
        edit.putInt(TimerState.KEY_CURRENT_STATUS, timer.status.ordinal)
        edit.apply()
    }

    fun restoreTimer(): TimerState {
        val currentTime = sharedPreferences.getLong(TimerState.KEY_CURRENT_TIME, 0)
        val totalTime = sharedPreferences.getLong(TimerState.KEY_TOTAL_TIME, 0)
        val currentStatusOrdinal = sharedPreferences.getInt(TimerState.KEY_CURRENT_STATUS, 0)

        return TimerState(currentTime, totalTime, TimerStatus.values()[currentStatusOrdinal])
    }


}