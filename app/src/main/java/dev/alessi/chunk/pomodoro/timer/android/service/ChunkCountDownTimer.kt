package dev.alessi.chunk.pomodoro.timer.android.service

import android.os.CountDownTimer
import kotlinx.coroutines.runBlocking

class ChunkCountDownTimer(
    totalInMillis: Long,
    ticketTimeMillis: Long,
    private val onTickCallback: (millisToFinish: Long, type: @Type Int) -> Unit,
    val onFinishCallback: () -> Unit,
    val type: @Type Int
) : CountDownTimer(totalInMillis, ticketTimeMillis) {

    var running = false


    fun startTimer() = runBlocking {
        super.start()

        running = true

    }

    fun stopTimer() = runBlocking {
        cancel()

        running = false
    }


    override fun onFinish() {

        onFinishCallback()

        running = false

    }

    override fun onTick(millisToFinish: Long) {
        this.onTickCallback(millisToFinish, type)
    }

    @Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.LOCAL_VARIABLE)
    annotation class Type {
        companion object {
            const val SLICE_TIMER = 0
            const val BREAK_TIMER = 1
        }
    }


}