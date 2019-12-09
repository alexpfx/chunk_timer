package dev.alessi.chunk.pomodoro.timer.android.platform

import android.os.CountDownTimer

class ChunkCountDownTimer(
    totalInMillis: Long,
    ticketTimeMillis: Long,
    private val onTickCallback: (millisToFinish: Long) -> Unit,
    val onFinishCallback: () -> Unit
) : CountDownTimer(totalInMillis, ticketTimeMillis) {


    override fun onFinish() {
        onFinishCallback()
    }

    override fun onTick(millisToFinish: Long) {


        this.onTickCallback(millisToFinish)
    }


}