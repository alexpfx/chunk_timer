package dev.alessi.chunk.pomodoro.timer.android.service

import android.os.CountDownTimer

class ChunkCountDownTimer(
    totalInMillis: Long,
    ticketTimeMillis: Long,
    private val onTickCallback: (millisToFinish: Long) -> Unit,
    val onFinishCallback: () -> Unit,
    val type: @Type Int
) : CountDownTimer(totalInMillis, ticketTimeMillis) {


    override fun onFinish() {
        onFinishCallback()
    }

    override fun onTick(millisToFinish: Long) {



        this.onTickCallback(millisToFinish)
    }

    @Target(AnnotationTarget.TYPE)
    annotation class Type{
        companion object{
            const val SLICE_TIMER = 0
            const val BREAK_TIMER = 1
        }
    }


}