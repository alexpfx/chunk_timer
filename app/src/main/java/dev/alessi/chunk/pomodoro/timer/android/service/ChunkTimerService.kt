package dev.alessi.chunk.pomodoro.timer.android.service

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import dev.alessi.chunk.pomodoro.timer.android.App
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnit
import dev.alessi.chunk.pomodoro.timer.android.repository.SliceRepository
import dev.alessi.chunk.pomodoro.timer.android.repository.SliceRepositoryProvider
import dev.alessi.chunk.pomodoro.timer.android.settings.SettingsFragment
import dev.alessi.chunk.pomodoro.timer.android.ui.TimerActivity
import dev.alessi.chunk.pomodoro.timer.android.util.*
import kotlinx.coroutines.*
import java.util.*


class ChunkTimerService : Service() {


    private var mOldStatus: Int = TimerState.status_ready
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
    private val mSoundEffectManager = SoundEffectManager(this)
    private lateinit var mSliceRepository: SliceRepository
    private val mScope = CoroutineScope(Dispatchers.Main)
    private var mBreaktimeRingIndex: Int = -1

    private var mTimerRingIndex = -1



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
        const val extra_param_old_status = "extra_param_old_status"
        const val extra_param_slice_id = "extra_param_slice_id"
        const val extra_param_current_time = "extra_param_current_time"

        const val extra_param_no_sound_on_tick = "extra_param_no_sound_on_tick"
        const val extra_param_a_timer_was_finish = "extra_param_a_timer_was_finish"
        const val extra_param_sizeIndex = "extra_param_sizeIndex"

        const val extra_param_task_id = "extra_param_task_id"

        val message_broadcast_message =
            ChunkTimerService::class.java.`package`.toString() + "BROADCAST_MESSAGE"

    }


    override fun onCreate() {
        loadSoundEffectPrefs()

        mSliceRepository = (application as SliceRepositoryProvider).sliceRepository

    }

    private fun loadSoundEffectPrefs() {
        val pm = PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
        mBreaktimeRingIndex = pm.getInt(SettingsFragment.pref_ring_breaktime, -1)
        mTimerRingIndex = pm.getInt(SettingsFragment.pref_ring_timer, -1)
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val command = handleCommandIntent(intent)

        if (Command.ACTION_REQUEST_STATE_UPDATE == command || Command.INVALID == command){
            return START_NOT_STICKY
        }


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


    private fun handleCommandIntent(intent: Intent): Int {
        val command = IntentBuilder.getCommand(intent)

        println("handleCommandIntent: $command")
        when (command) {
            Command.ACTION_START_TIMER -> commandStart(intent)
            Command.ACTION_START_BREAK -> commandStartBreak(intent)
            Command.ACTION_STOP -> commandStop()
            Command.ACTION_REQUEST_STATE_UPDATE -> commandRequestStatusUpdate()
        }

        return command


    }


    private fun commandRequestStatusUpdate() {

        broadcastState()
    }


    private fun commandStartBreak(intent: Intent) {
        if (!mServiceStarted) {
            runTheService(intent)
        } else {
            mTotalTime = intent.getLongExtra(extra_param_total_time_millis, 10 * 60 * 1000)
            mChunckTimer = createTimer(mTotalTime).also { it.start() }
//            startForeground(ongoing_notification_break_id, getNotification())

            setNewStatus(TimerState.status_running_break)

        }

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




//        startForeground(ongoing_notification_id, getNotification())

//        broadCastTimerStarted(Command.ACTION_START_TIMER)
        setNewStatus(TimerState.status_running_timer)

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

        setNewStatus(TimerState.status_ready)




    }

    private fun setNewStatus(newStatus: Int) {


        mOldStatus = mStatus
        mStatus = newStatus

        println("setNewStatus $mOldStatus $mStatus")
        broadcastState()
    }


//    //TODO fazer este controle pelo update status
//    private fun broadcastTimerStopped() {
//        val bundle = Bundle()
//
//        bundle.putInt(extra_param_status, mStatus)
//        bundle.putLong(extra_param_total_time_millis, mTotalTime)
//        bundle.putLong(extra_param_current_time, mTotalTime)
//
//        //TODO last index deve ser setado quando troca o timer
//        val lastIndex = getPreference().getInt(TimerFragment.KEY_LAST_INDEX, 2)
//        bundle.putInt(extra_param_last_index, lastIndex)
//        val intent =
//            IntentBuilder.getIntentForAction(message_broadcast_message, Command.ACTION_STOP, bundle)
//
//        sendBroadcast(intent)
//
//    }

//    //TODO fazer este controle pelo update status
//    private fun broadCastTimerStarted(@Command command: Int) {
//        val bundle = Bundle()
//        bundle.putInt(extra_param_status, mStatus)
//
//        val intent = IntentBuilder.getIntentForAction(
//            message_broadcast_message,
//            command,
//            bundle
//        )
//
////        sendBroadcast(intent)
//        broadcastState()
//    }

    private fun broadcastState() {
        val extras = Bundle()
        extras.putInt(extra_param_old_status, mOldStatus)
        extras.putInt(extra_param_status, mStatus)

        extras.putLong(extra_param_current_time, mCurrentTime)
        extras.putLong(extra_param_total_time_millis, mTotalTime)

        val intent =
            IntentBuilder.getIntentForAction(
                message_broadcast_message,
                Command.ACTION_REQUEST_STATE_UPDATE,
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
        val h = Handler()
        h.postDelayed({
            mSoundEffectManager.dispose()
        }, 1000)

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

//        broadcastTick()
        setNewStatus(mStatus)
        broadcastState()
        mNotificationManagerCompat.notifyTick(mCurrentTime, applicationContext, mStatus)
    }



    private fun onTimerFinish() {
        mNotificationManagerCompat.notifyTimerFinish(
            applicationContext
        )

        mSoundEffectManager.play(mTimerRingIndex)
        saveSlice()
    }

    private fun afterSliceSaved(id: Int) {
        // chamar tela que carregará o id.

        val args = Bundle()
        args.putInt(extra_param_status, TimerState.status_running_timer)


        val timerActivityIntent = Intent(this, TimerActivity::class.java)
        timerActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        timerActivityIntent.putExtra(extra_param_a_timer_was_finish, true)
        timerActivityIntent.putExtra(extra_param_slice_id, id)
        timerActivityIntent.putExtras(args)

        startActivity(timerActivityIntent)

    }

    private fun onFinish() {
        val oldState = mStatus
        commandStop()

        when (oldState) {
            TimerState.status_running_timer -> {
                onTimerFinish()


//                val dialogIntent = Intent(this, TimerActivity::class.java)

//                val args = Bundle()
//                args.putLong(extra_param_total_time_millis, mTotalTime)
//                args.putInt(extra_param_status, oldState)
//
//                dialogIntent.putExtra(extra_param_total_time_millis, mTotalTime)
//                dialogIntent.putExtra(extra_param_task_id, mTaskId)
//                dialogIntent.putExtra(extra_param_sizeIndex, mSizeIndex)
//
//                dialogIntent.putExtra(extra_param_a_timer_was_finish, true)
//                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(dialogIntent)


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


    private fun saveSlice() {
        mScope.launch {
            val id = withContext(Dispatchers.IO) {
                mSliceRepository.storeSlice(
                    WorkUnit(
                        sizeId = mSizeIndex!!,
                        timeMinutes = (mTotalTime / 60 / 1000).toInt(),
                        taskId = mTaskId!!,
                        finishDate = Date(),
                        estimation = 0
                    )
                )
            }

            afterSliceSaved(id)

        }
    }



    override fun onBind(intent: Intent): IBinder? {
        return null
    }

}


