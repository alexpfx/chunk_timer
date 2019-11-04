package dev.alessi.chunk.pomodoro.timer.android.util

import android.util.Log

fun Any.debug(message: String) {
    Log.d(this.javaClass.name, message)
}