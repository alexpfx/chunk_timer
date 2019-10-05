package dev.alessi.chunk.pomodoro.timer.android

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ChunkTimerService : Service() {

    companion object {
        const val EXTRA_ACTION_START = 0
        const val EXTRA_ACTION_STOP = 1
        const val EXTRA_ACTION_SETUP = 2
        const val EXTRA_TOTAL_TIME = "EXTRA_TOTAL_TIME"
        const val EXTRA_ACTION = "EXTRA_ACTION"
        const val EXTRA_TIME_LEFT = "EXTRA_TIME_LEFT"
        const val EXTRA_TIME_CURRENT_STATE = "EXTRA_TIME_CURRENT_STATE"

        val TICK_BROADCAST = ChunkTimerService::class.java.`package`.toString()+"TICK_MESSAGE"

    }

    private var mTotalTime: Long = 0
    lateinit var mChunckTimer: ChunkCountDownTimer
    private var mTimeLeft: Long = 0

    var mCurrentState = EXTRA_ACTION_STOP


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mCurrentState = intent.getIntExtra(EXTRA_ACTION, EXTRA_ACTION_SETUP)
        mTotalTime = intent.getLongExtra(EXTRA_TOTAL_TIME, 24L)

        startTimer()

        return super.onStartCommand(intent, flags, startId)
    }

    private fun onTick(timeLeft: Long) {
        this.mTimeLeft = timeLeft
        broadcastTick()



    }

    private fun broadcastTick() {
        val intent = Intent(TICK_BROADCAST)
        intent.putExtra(EXTRA_TIME_LEFT, mTimeLeft)
        intent.putExtra(EXTRA_TIME_CURRENT_STATE, mCurrentState)
        intent.putExtra(EXTRA_TOTAL_TIME, mTotalTime)
        sendBroadcast(intent)
    }

    private fun onFinish() {

    }

    private fun startTimer() {
        mChunckTimer = ChunkCountDownTimer(
            totalInMillis = (mTotalTime),
            ticketTimeMillis = 1000,
            onTickCallback = ::onTick,
            onFinishCallback = ::onFinish
        )

        mChunckTimer.start()

    }

    override fun onBind(intent: Intent): IBinder? {
        return null


    }
}
