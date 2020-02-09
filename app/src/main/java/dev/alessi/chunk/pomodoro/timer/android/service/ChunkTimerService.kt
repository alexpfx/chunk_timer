package dev.alessi.chunk.pomodoro.timer.android.service

import android.app.Service
import android.content.Intent
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
import dev.alessi.chunk.pomodoro.timer.android.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class ChunkTimerService : Service() {
    private var mServiceRunning = false


    private var mCurrentTime: Long = 0
    private var mTotalTime: Long = 0
    private lateinit var mChunckTimer: ChunkCountDownTimer
    private var mNotificationManagerCompat: NotificationManagerCompat? = null
    private var mTaskId: Int? = null

    //    private var mTaskname: String? = null
    private var mSizeIndex: Int? = null
    private val mSoundEffectManager = SoundEffectManager(this)
    private var mSliceRepository: SliceRepository? = null
    private val mScope = CoroutineScope(Dispatchers.Main)
    private var mBreaktimeRingIndex: Int = -1

    private var mTimerRingIndex = -1


    companion object {
        const val extra_param_tick_type = "extra_param_tick_type"
        const val extra_param_total_time_millis = "param_total_time"
        const val extra_param_event = "extra_param_event"
        const val extra_param_command = "extra_param_command"

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

        if (!mServiceRunning && (Command.ACTION_START_BREAKTIME == command || Command.ACTION_START_TIME_SLICE == command)) {
            mNotificationManagerCompat = NotificationManagerCompat.from(this)
            startForeground(
                App.NOTIFICATION_ID,
                mNotificationManagerCompat?.getForegroundNotification(applicationContext)
            )
            mServiceRunning = true
        }

        return START_NOT_STICKY
    }


    private fun handleCommandIntent(intent: Intent): Int {
        val command = IntentBuilder.getCommand(intent)

        when (command) {
            Command.ACTION_STOP_SERVICE -> stopService(Event.ON_SERVICE_STOPPED)
            Command.ACTION_START_TIME_SLICE -> startClockTimer(intent, Event.ON_TIMER_STARTED)
            Command.ACTION_START_BREAKTIME -> startClockTimer(intent, Event.ON_BREAKTIME_STARTED)
            Command.ACTION_COMPLETE_TIME_SLICE -> onTimeSliceCompleted()
            Command.ACTION_COMPLETE_BREAKTIME -> onBreaktimeCompleted()
            Command.ACTION_REPEAT_LAST_EVENT -> commandRequestStatusUpdate()
        }

        return command

    }

    private fun commandRequestStatusUpdate() {
        if (this::mChunckTimer.isInitialized && mChunckTimer.running) {
            broadcastEvent(Event.ON_TICK, tickType = mChunckTimer.type)
        } else {
            broadcastEvent(Event.ON_SERVICE_STOPPED)
        }

    }

    private fun startClockTimer(intent: Intent, event: @Event Int) {
        if (!mServiceRunning) {
            runTheService(intent)
            return
        }

        mTotalTime = intent.getLongExtra(extra_param_total_time_millis, 24 * 60 * 1000)
        mTaskId = intent.getIntExtra(extra_param_task_id, -1)
        mSizeIndex = intent.getIntExtra(extra_param_sizeIndex, -1)


        mChunckTimer = createTimer(mTotalTime, event).also { it.startTimer() }

        broadcastEvent(event)

    }

    private fun runTheService(intent: Intent) {
        ContextCompat.startForegroundService(
            this, intent
        )

    }

    private fun stopService(event: @Event Int) {
        mNotificationManagerCompat?.cancelAll()

        if (this::mChunckTimer.isInitialized){
            mChunckTimer.stopTimer()
        }


        stopForeground(true)
        stopSelf()

        broadcastEvent(event)

        mServiceRunning = false

    }


    private fun onBreaktimeCompleted() {
        mSoundEffectManager.play(mBreaktimeRingIndex)

        mNotificationManagerCompat?.notifyBreakFinish(
            applicationContext
        )

        val extras = Bundle().apply {
            putInt(extra_param_event, Event.ON_BREAKTIME_COMPLETED)
            putLong(extra_param_total_time_millis, mTotalTime)
        }

        val intent = IntentBuilder.getIntentForActivity(applicationContext, extras)
        startActivity(intent)


    }

    private fun onTimeSliceCompleted() {
        mSoundEffectManager.play(mTimerRingIndex)
        saveAndNotify()

    }


    //    private fun onTimeSliceCompleted() {
//        mNotificationManagerCompat?.notifyTimerFinish(
//            applicationContext
//        )
//
//        mSoundEffectManager.play(mTimerRingIndex)
//
//
//        afterSliceSaved(27)
//        broadcastEvent(Event.ON_TIME_SLICE_COMPLETED, 27)
//
////        val id = saveSlice()
//
//
////        save().observeForever {
////            it?.let {
////                afterSliceSaved(it)
////                broadcastEvent(Event.ON_TIME_SLICE_COMPLETED, it)
////            }
////
////        }
//
//
//
//    }

//    private fun onBreaktimeCompleted() {
//        mSoundEffectManager.play(mBreaktimeRingIndex)
//
//        broadcastEvent(Event.ON_BREAKTIME_COMPLETED)
//
//        stopService(Event.ON_SERVICE_STOPPED)
//
//    }


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

    private fun broadcastEvent(event: @Event Int, sliceId: Int? = null, @ChunkCountDownTimer.Type tickType: Int? = null) {
        val intent =
            createBundleForEvent(event, tickType, sliceId)

        if (event != Event.ON_TICK) {

        }

        sendBroadcast(intent)
    }

    private fun createBundleForEvent(
        event: @Event Int,
        tickType: Int?,
        sliceId: Int?
    ): Intent {
        val extras = Bundle()

        extras.putInt(extra_param_event, event)
        extras.putLong(extra_param_current_time, mCurrentTime)
        extras.putLong(extra_param_total_time_millis, mTotalTime)

        tickType?.let {
            extras.putInt(extra_param_tick_type, tickType)
        }

        sliceId?.let {
            extras.putInt(extra_param_slice_id, sliceId)
        }

        val intent =
            IntentBuilder.getIntentForEvent(
                message_broadcast_message,
                event,
                extras
            )
        return intent
    }

    private fun broadcastTick(silentTick: Boolean = false) {
        val extras = Bundle()
//        extras.putInt(extra_param_event, event)
        extras.putLong(extra_param_current_time, mCurrentTime)
        extras.putLong(extra_param_total_time_millis, mTotalTime)
        if (silentTick) {
            extras.putBoolean(extra_param_no_sound_on_tick, silentTick)
        }

        val intent =
            IntentBuilder.getIntentForEvent(message_broadcast_message, Event.ON_TICK, extras)

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


    private fun createTimer(
        totalTime: Long,
        event: @Event Int
    ): ChunkCountDownTimer {
        val type: @ChunkCountDownTimer.Type Int =
            if (event == Event.ON_TIMER_STARTED) ChunkCountDownTimer.Type.SLICE_TIMER else ChunkCountDownTimer.Type.BREAK_TIMER

        return ChunkCountDownTimer(
            totalInMillis = (totalTime),
            ticketTimeMillis = 1000,
            onTickCallback = ::onTick,
            onFinishCallback = ::onClockFinish,
            type = type
        )
    }

    private fun onTick(timeLeft: Long, type: @ChunkCountDownTimer.Type Int) {
        mCurrentTime = timeLeft

        broadcastEvent(Event.ON_TICK, tickType = type)

        mChunckTimer?.let {
            mNotificationManagerCompat?.notifyTick(mCurrentTime, applicationContext, mapTypeToEvent(it.type))
        }
    }

    private fun mapTypeToEvent(type: @ChunkCountDownTimer.Type Int): @Event Int {
        return if (type == ChunkCountDownTimer.Type.SLICE_TIMER) Event.ON_TIMER_STARTED else return Event.ON_BREAKTIME_STARTED
    }


    private fun afterSliceSaved(id: Int?) {
        mNotificationManagerCompat?.notifyTimerFinish(
            applicationContext
        )

        val extras = Bundle().apply {
            putInt(extra_param_event, Event.ON_TIME_SLICE_COMPLETED)
            id?.let {
                putInt(extra_param_slice_id, it)
            }
        }

        val intent = IntentBuilder.getIntentForActivity(applicationContext, extras)
        startActivity(intent)

    }


    private fun onClockFinish() {
        if (!this::mChunckTimer.isInitialized){
            return
        }

        val command = if (mChunckTimer.type == ChunkCountDownTimer.Type.SLICE_TIMER)
            Command.ACTION_COMPLETE_TIME_SLICE
        else Command.ACTION_COMPLETE_BREAKTIME

        val intent = IntentBuilder.getIntentForService(this, command)
        startService(intent)

    }


    private fun saveAndNotify() {

        mScope.launch {
            val id = withContext(Dispatchers.IO) {
                mSliceRepository?.storeSlice(
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


    @Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.LOCAL_VARIABLE)
    annotation class Event {
        companion object {
            const val ON_TIMER_STARTED = 0
            const val ON_TIME_SLICE_COMPLETED = 1
            const val ON_BREAKTIME_STARTED = 2
            const val ON_BREAKTIME_COMPLETED = 3
            const val ON_SERVICE_STOPPED = 4
            const val ON_TICK = 5
        }
    }

    @Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION, AnnotationTarget.LOCAL_VARIABLE)
    annotation class Command {
        companion object {
            const val INVALID = -1
            const val ACTION_START_TIME_SLICE = 0
            const val ACTION_COMPLETE_TIME_SLICE = 1
            const val ACTION_START_BREAKTIME = 2
            const val ACTION_COMPLETE_BREAKTIME = 3
            const val ACTION_REPEAT_LAST_EVENT = 4
            const val ACTION_STOP_SERVICE = 5
        }
    }


}


