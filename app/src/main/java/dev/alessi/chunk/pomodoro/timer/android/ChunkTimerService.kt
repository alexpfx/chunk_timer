package dev.alessi.chunk.pomodoro.timer.android

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import dev.alessi.TimerControl
import kotlin.math.roundToLong

class ChunkTimerService : Service(), TimerControl {



    companion object {
        const val STATE_READY_POMODORO = 0
        const val STATE_RUNNING_POMODORO = 1
        const val STATE_READY_BREAK = 2
        const val STATE_RUNNING_BREAK = 3

        const val EXTRA_TOTAL_TIME = "EXTRA_TOTAL_TIME"
        const val EXTRA_ACTION = "EXTRA_ACTION"
        const val EXTRA_TIME_LEFT = "EXTRA_TIME_LEFT"
        const val EXTRA_TIME_CURRENT_STATE = "EXTRA_TIME_CURRENT_STATE"

        val TICK_BROADCAST = ChunkTimerService::class.java.`package`.toString() + "TICK_MESSAGE"

    }

    private var mTotalTime: Long = 0
    private var mTotalBreakTime: Long = 0
    private lateinit var mChunckTimer: ChunkCountDownTimer
    private var mTimeLeft: Long = 0
    private var mCurrentState = STATE_READY_POMODORO
    private var view: TimerView? = null
    private val binder: LocalBinder = LocalBinder()


    override fun onSetup(totalTime: Long) {
        mTotalTime = totalTime
    }


    override fun onCreate() {
        super.onCreate()
    }




    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        onSetup(intent.getLongExtra(EXTRA_TOTAL_TIME, 24L))
        return super.onStartCommand(intent, flags, startId)
    }


    private fun onTick(timeLeft: Long) {
        this.mTimeLeft = timeLeft
        broadcastTick()
    }

    private fun stopTimer() {


        mChunckTimer.cancel()
        mCurrentState = STATE_READY_POMODORO
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
        mCurrentState = STATE_READY_POMODORO
    }



    override fun startTimer() {
        mCurrentState = STATE_RUNNING_POMODORO

        mChunckTimer = createTimer(mTotalTime).also { it.start() }

        view?.showTimerStarted()

    }

    private fun createTimer(totalTime: Long): ChunkCountDownTimer {
        return ChunkCountDownTimer(
            totalInMillis = (totalTime),
            ticketTimeMillis = 1000,
            onTickCallback = ::onTick,
            onFinishCallback = ::onFinish
        )
    }


    private fun getBreakForTimer(): Long{
        val divisor = 0.2
        return (mTotalTime / divisor).roundToLong()
    }

    override fun startBreak() {
        mCurrentState = STATE_RUNNING_BREAK
        mTotalBreakTime = getBreakForTimer()

        createTimer(mTotalBreakTime).also { it.start() }

        view?.showBreakTimerStarted()

    }

    override fun cancelTimer() {
        mCurrentState = STATE_READY_POMODORO

        mChunckTimer.cancel()
        view?.showTimerCanceled()

    }

    override fun cancelBreak() {
        mCurrentState = STATE_READY_POMODORO


        mChunckTimer.cancel()
        view?.showBreakTimerCanceled()

    }

    override fun onFinishTimer() {
        mCurrentState = STATE_READY_BREAK
    }

    override fun onFinishBreak() {
        mCurrentState = STATE_READY_POMODORO
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService(): TimerControl = this@ChunkTimerService
    }

    override fun onBound(view: TimerView) {
        this.view = view
    }

    override fun onUnBound() {
        this.view = null
    }

    override fun doStartService(intent: Intent) {
        startService(intent)
    }

    override fun doStopService(intent: Intent) {
        stopService(intent)
    }


}
