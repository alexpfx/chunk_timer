package dev.alessi.chunk.pomodoro.timer.android.util

import android.text.format.DateUtils

fun Long.toFormatedTime(): String {
    return DateUtils.formatElapsedTime(this / 1000)
}