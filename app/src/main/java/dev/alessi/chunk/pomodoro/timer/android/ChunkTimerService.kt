package dev.alessi.chunk.pomodoro.timer.android

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ChunkTimerService : Service() {

    companion object {
        const val STATE_RUNNING = 0
        const val STATE_READY = 1


        const val EXTRA_TOTAL_TIME = "EXTRA_TOTAL_TIME"
        const val EXTRA_ACTION = "EXTRA_ACTION"
        const val EXTRA_TIME_LEFT = "EXTRA_TIME_LEFT"
        const val EXTRA_TIME_CURRENT_STATE = "EXTRA_TIME_CURRENT_STATE"




        val TICK_BROADCAST = ChunkTimerService::class.java.`package`.toString() + "TICK_MESSAGE"

    }

    private var mTotalTime: Long = 0
    private lateinit var mChunckTimer: ChunkCountDownTimer
    private var mTimeLeft: Long = 0

    private var mCurrentState = STATE_READY


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mTotalTime = intent.getLongExtra(EXTRA_TOTAL_TIME, 24L)

        startTimer()

        return super.onStartCommand(intent, flags, startId)
    }


    private fun onTick(timeLeft: Long) {
        this.mTimeLeft = timeLeft
        broadcastTick()
    }

    private fun stopTimer(){
        mChunckTimer.cancel()
        mCurrentState = STATE_READY


    }


    override fun onDestroy() {
        stopTimer()
        stopSelf()
        super.onDestroy()
    }

    private fun broadcastTick() {
        val intent = Intent(TICK_BROADCAST)
        intent.putExtra(EXTRA_TIME_LEFT, mTimeLeft)
        intent.putExtra(EXTRA_TIME_CURRENT_STATE, mCurrentState)
        intent.putExtra(EXTRA_TOTAL_TIME, mTotalTime)
        sendBroadcast(intent)
    }



    private fun onFinish() {
        mCurrentState = STATE_READY


    }


    private fun startTimer() {
        mCurrentState = STATE_RUNNING

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
