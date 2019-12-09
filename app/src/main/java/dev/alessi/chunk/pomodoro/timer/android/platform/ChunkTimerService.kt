package dev.alessi.chunk.pomodoro.timer.android.platform

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import dev.alessi.chunk.pomodoro.timer.android.App
import dev.alessi.chunk.pomodoro.timer.android.ui.TimerActivity
import dev.alessi.chunk.pomodoro.timer.android.ui.TimerFragment
import dev.alessi.chunk.pomodoro.timer.android.util.*


class ChunkTimerService : Service() {


    private var mServiceStarted = false
    private var mStatus: @TimerState Int =
        TimerState.status_ready
    private var mCurrentTime: Long = 0
    private var mTotalTime: Long = 0
    private lateinit var mChunckTimer: ChunkCountDownTimer
    private lateinit var mNotificationManagerCompat: NotificationManagerCompat
    private var mTaskId: Int? = null

    //    private var mTaskname: String? = null
    private var mSizeIndex: Int? = null


    @Target(
        AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER,
        AnnotationTarget.LOCAL_VARIABLE
    )
    annotation class TimerState {

        companion object {
            const val status_ready = 0
            const val status_running_timer = 1
            const val status_running_break = 2
        }
    }

    companion object {

        const val extra_param_total_time_millis = "param_total_time"
        const val extra_param_status = "extra_param_status"
        const val extra_param_current_time = "extra_param_current_time"
        const val extra_param_last_index = "extra_param_last_index"
        const val extra_param_no_sound_on_tick = "extra_param_no_sound_on_tick"
        const val extra_param_a_timer_was_finish = "extra_param_a_timer_was_finish"
        const val extra_param_sizeIndex = "extra_param_sizeIndex"

        const val extra_param_task_id = "extra_param_task_id"

        val message_broadcast_message =
            ChunkTimerService::class.java.`package`.toString() + "BROADCAST_MESSAGE"

    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        processIntent(intent)

        mNotificationManagerCompat = NotificationManagerCompat.from(this)


        if (!mServiceStarted) {
            startForeground(
                App.NOTIFICATION_ID,
                mNotificationManagerCompat.getForegroundNotification(applicationContext)
            )
            mServiceStarted = true
        }



        return START_NOT_STICKY
    }


    private fun processIntent(intent: Intent) {

        when (IntentBuilder.getCommand(intent)) {
            Command.ACTION_START_TIMER -> commandStart(intent)
            Command.ACTION_START_BREAK -> commandStartBreak(intent)
            Command.ACTION_STOP -> commandStop()
            Command.ACTION_REQUEST_TICK -> requestTick()
            Command.ACTION_UPDATE_STATE -> commandUpdateStatus()
        }

    }


    private fun commandUpdateStatus() {
        if (mStatus != TimerState.status_ready)
            broadcastState()
    }


    private fun requestTick() {
        if (mServiceStarted && (mStatus == TimerState.status_running_timer || mStatus == TimerState.status_running_break)) {
            broadcastTick(silentTick = true)
            mNotificationManagerCompat.notifyTick(mCurrentTime, applicationContext, mStatus)
            debug("requestTick")
        }
    }

    private fun commandStartBreak(intent: Intent) {
        if (!mServiceStarted) {
            runTheService(intent)
        } else {
            mTotalTime = intent.getLongExtra(extra_param_total_time_millis, 10 * 60 * 1000)
            mChunckTimer = createTimer(mTotalTime).also { it.start() }
            mStatus =
                TimerState.status_running_break
//            startForeground(ongoing_notification_break_id, getNotification())
            broadCastTimerStarted(Command.ACTION_START_BREAK)

        }

    }

    private fun getPreference(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(this)
    }

    private fun commandStart(intent: Intent) {
        if (!mServiceStarted) {
            runTheService(intent)
            return
        }

        mTotalTime = intent.getLongExtra(extra_param_total_time_millis, 24 * 60 * 1000)
//        mTaskname = intent.getStringExtra(extra_param_taskname)
        mTaskId = intent.getIntExtra(extra_param_task_id, -1)

        mSizeIndex = intent.getIntExtra(extra_param_sizeIndex, -1)

        mChunckTimer = createTimer(mTotalTime).also { it.start() }

        mStatus =
            TimerState.status_running_timer
//        startForeground(ongoing_notification_id, getNotification())

        broadCastTimerStarted(Command.ACTION_START_TIMER)
    }

    private fun runTheService(intent: Intent) {
        ContextCompat.startForegroundService(
            this, intent
        )

    }

    private fun commandStop() {
        mNotificationManagerCompat.cancelAll()
        mChunckTimer.cancel()
        stopForeground(true)
        stopSelf()
        mServiceStarted = false
        mStatus =
            TimerState.status_ready

        broadcastTimerStopped()

    }

    private fun broadcastTimerStopped() {
        val bundle = Bundle()

        bundle.putInt(extra_param_status, mStatus)
        bundle.putLong(extra_param_total_time_millis, mTotalTime)
        bundle.putLong(extra_param_current_time, mTotalTime)

        //TODO
        val lastIndex = getPreference().getInt(TimerFragment.KEY_LAST_INDEX, 2)
        bundle.putInt(extra_param_last_index, lastIndex)
        val intent =
            IntentBuilder.getIntentForAction(message_broadcast_message, Command.ACTION_STOP, bundle)

        sendBroadcast(intent)

    }

    private fun broadCastTimerStarted(@Command command: Int) {
        val bundle = Bundle()
        bundle.putInt(extra_param_status, mStatus)

        val intent = IntentBuilder.getIntentForAction(
            message_broadcast_message,
            command,
            bundle
        )

        sendBroadcast(intent)
    }

    private fun broadcastState() {
        val extras = Bundle()
        extras.putInt(extra_param_status, mStatus)
        extras.putLong(extra_param_current_time, mCurrentTime)
        extras.putLong(extra_param_total_time_millis, mTotalTime)

        val intent =
            IntentBuilder.getIntentForAction(
                message_broadcast_message,
                Command.ACTION_UPDATE_STATE,
                extras
            )

        sendBroadcast(intent)
    }

    private fun broadcastTick(silentTick: Boolean = false) {
        val extras = Bundle()
        extras.putInt(extra_param_status, mStatus)
        extras.putLong(extra_param_current_time, mCurrentTime)
        extras.putLong(extra_param_total_time_millis, mTotalTime)
        if (silentTick) {
            extras.putBoolean(extra_param_no_sound_on_tick, silentTick)
        }

        val intent =
            IntentBuilder.getIntentForAction(message_broadcast_message, Command.ACTION_TICK, extras)

        sendBroadcast(intent)


    }


    override fun onDestroy() {
        stopSelf()




        super.onDestroy()
    }


    private fun createTimer(totalTime: Long): ChunkCountDownTimer {
        return ChunkCountDownTimer(
            totalInMillis = (totalTime),
            ticketTimeMillis = 1000,
            onTickCallback = ::onTick,
            onFinishCallback = ::onFinish
        )
    }

    private fun onTick(timeLeft: Long) {
        mCurrentTime = timeLeft

        broadcastTick()
        mNotificationManagerCompat.notifyTick(mCurrentTime, applicationContext, mStatus)
    }

    private fun onFinish() {
        val oldState = mStatus
        commandStop()

        when (oldState) {
            TimerState.status_running_timer -> {
                mNotificationManagerCompat.notifyTimerFinish(
                    applicationContext
                )

                val dialogIntent = Intent(this, TimerActivity::class.java)

                val args = Bundle()
                args.putLong(extra_param_total_time_millis, mTotalTime)
                args.putInt(extra_param_status, oldState)

                dialogIntent.putExtra(extra_param_total_time_millis, mTotalTime)
                dialogIntent.putExtra(extra_param_task_id, mTaskId)
                dialogIntent.putExtra(extra_param_sizeIndex, mSizeIndex)

                dialogIntent.putExtra(extra_param_a_timer_was_finish, true)
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(dialogIntent)


            }
            TimerState.status_running_break -> {

                mNotificationManagerCompat.notifyBreakFinish(
                    applicationContext
                )

                val args = Bundle()
                args.putLong(extra_param_total_time_millis, mTotalTime)
                args.putInt(extra_param_status, oldState)

                val dialogIntent = Intent(this, TimerActivity::class.java)

                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                dialogIntent.putExtras(args)
                dialogIntent.putExtra(extra_param_a_timer_was_finish, true)
                startActivity(dialogIntent)


            }
        }

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

}


