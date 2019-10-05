//package dev.alessi.chunk.pomodoro.timer.android
//
//import Constants
//import android.app.AlarmManager
//import android.app.PendingIntent
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.os.Build
//import android.os.Bundle
//import android.text.format.DateUtils
//import android.util.Log
//import android.view.Menu
//import android.view.MenuInflater
//import android.view.MenuItem
//import android.view.View
//import androidx.appcompat.app.AppCompatActivity
//import androidx.preference.PreferenceManager
//import com.google.android.material.badge.BadgeDrawable
//import com.google.android.material.button.MaterialButton
//import kotlinx.android.synthetic.main.activity_main.*
//import timeMap
//
//
//fun Any.debug(message: String) {
//    Log.d(this.javaClass.simpleName, message)
//}
//
///* millis to seconds */
//fun Long.toSeconds(): Long {
//    return this / 1000
//}
//
///* minutes to millis */
//fun Long.fromMinutes(): Long{
//    return this * 60 * 1000
//}
//
//// Criar ViewModel
//class MainActivity : AppCompatActivity(), PomodoroView {
//    private var status: TimerStatus = TimerStatus.ready
//
//    private var totalTimeInMinutes = 0L
//
//
//    private var alarmManager: AlarmManager? = null
//    private lateinit var sizeButtons: List<MaterialButton>
//    //    private lateinit var presenter: PomodoroPresenter
//    private lateinit var sfxManager: SoundEffectManager
//    private lateinit var timerServiceIntenet: Intent
//
//    override fun restoreTimer() {
//    }
//
//    private val TIME_REMAINING_KEY = "TIMER_REMAINING_TIME_KEY"
//
//    override fun persistTimer(remainingTime: Long) {
//        addAlarm(System.currentTimeMillis() + remainingTime)
//
//        val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()
//        editor.putLong(TIME_REMAINING_KEY, remainingTime)
//        editor.apply()
//    }
//
//    override fun onStop() {
////        presenter.onStop()
//        super.onStop()
//    }
//
//    override fun onStart() {
//        removeAlarm()
//
//        val pref = PreferenceManager.getDefaultSharedPreferences(this)
//        val remainingTime = pref.getLong(TIME_REMAINING_KEY, 0)
//
////        presenter.onStart(remainingTime)
//        super.onStart()
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//
//        super.onRestoreInstanceState(savedInstanceState)
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//
//        super.onSaveInstanceState(outState)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        sfxManager = SoundEffectManager(this)
//
//        viewInit()
//
//        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//
//
//    }
//
//
//
//    private fun addAlarm(wakeTime: Long) {
//        val rtcWakeUp = AlarmManager.RTC_WAKEUP
//
//        val intent = Intent(this, TimeUpAlarmReceiver::class.java)
//        val piAlarm = PendingIntent.getBroadcast(this, 0, intent, 0)
//
//
//        when {
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> alarmManager?.setExactAndAllowWhileIdle(
//                rtcWakeUp,
//                wakeTime,
//                piAlarm
//            )
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> alarmManager?.setExact(
//                AlarmManager.RTC_WAKEUP,
//                wakeTime,
//                piAlarm
//            )
//            else -> alarmManager?.set(AlarmManager.RTC_WAKEUP, wakeTime, piAlarm)
//        }
//
//    }
//
//    fun removeAlarm() {
//        val intent = Intent(this, TimeUpAlarmReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
//        alarmManager?.cancel(pendingIntent)
//
//    }
//
//    fun onToggleActionClick(view: View) {
//        if (status == TimerStatus.ready) {
//            status = TimerStatus.running
//            startTimerService()
//        } else {
//            status = TimerStatus.ready
//            stopTimer()
//        }
//
//    }
//
//
//    private fun viewInit() {
//        this.sizeButtons = listOf(
//            btnSizePP,
//            btnSizeP,
//            btnSizeM,
//            btnSizeG,
//            btnSizeGG
//        )
//        initTags()
//
//
//    }
//
//    var timerReceiver: TimerReceiver? = null
//
//    override fun onResume() {
//        if (timerReceiver == null) {
//            timerReceiver = TimerReceiver(this)
//            registerReceiver(timerReceiver, IntentFilter(ChunkTimerService.TICK_BROADCAST_MESSAGE))
//        }
//
//
//        super.onResume()
//    }
//
//    override fun onPause() {
//        if (timerReceiver != null) {
//            unregisterReceiver(timerReceiver)
//            timerReceiver = null
//        }
//        super.onPause()
//    }
//
//    private fun initTags() {
//        sizeButtons.forEachIndexed { index, button ->
//            run {
//
//                print(button.text)
//                button.tag = index
//
//            }
//        }
//    }
//
//    override fun showTick(totalTimeInSeconds: Long, secondsToFinish: Long) {
//        val badge = BadgeDrawable.create(this)
//        badge.number = 40
//
////        BadgeUtils.attachBadgeDrawable(badge, txtTimerDisplay, frame_timer)
//
//        val formatedTime = DateUtils.formatElapsedTime(secondsToFinish)
//        txtTimerDisplay.text = formatedTime
//        sfxManager.playTick()
//        val percFinish = secondsToFinish * 100 / totalTimeInSeconds
//        chip2.text = percFinish.toString().plus(" %")
//        progress(percFinish.toInt())
//
//    }
//
//    override fun showTimerSetted(seconds: Long) {
//        val formatedTime = DateUtils.formatElapsedTime(seconds)
//        txtTimerDisplay.text = formatedTime
//        progress(100)
//    }
//
//    fun progress(value: Int) {
//        progressBar.progress = value
//    }
//
//    override fun showTimerFinished() {
//        sfxManager.playFinish()
//    }
//
//    override fun showSetupChanged(tag: Int) {
//        uncheckAll()
//        checkThis(tag)
//    }
//
//
//    fun changeSize(view: View) {
//        totalTimeInMinutes = timeMap[view.tag]!!
//    }
//
//    override fun showStatusReady() {
//        enableAll()
//        btnTimerAction.setIconResource(R.drawable.ic_play_arrow_black_24dp)
//        btnTimerAction.setText(R.string.button_label_start)
//        chip2.visibility = View.INVISIBLE
//
//    }
//
//    override fun showStatusRunning() {
//        disableAll()
//        btnTimerAction.setIconResource(R.drawable.ic_stop_black_24dp)
//        btnTimerAction.setText(R.string.button_label_cancel)
//        sfxManager.playStart()
//        chip2.visibility = View.VISIBLE
//    }
//
//    private fun disableAll() {
//        sizeButtons.forEach {
//            it.isEnabled = false
//        }
//    }
//
//    private fun enableAll() {
//        sizeButtons.forEach {
//            it.isEnabled = true
//        }
//
//    }
//
//    private fun uncheckAll() {
//        sizeButtons.forEach {
//            it.isChecked = false
//        }
//    }
//
//    private fun checkThis(tag: Int) {
//        getSizedButtonByTagId(tag).isChecked = true
//    }
//
//    private fun getSizedButtonByTagId(id: Int): MaterialButton {
//        return this.sizeButtons.first {
//            it.tag == id
//        }
//
//    }
//
//    private fun startTimerService() {
//
//        timerServiceIntenet = Intent(this, ChunkTimerService::class.java)
//        timerServiceIntenet.putExtra(
//            ChunkTimerService.EXTRA_TOTAL_TIME,
//            totalTimeInMinutes.fromMinutes()
//        )
//        startService(timerServiceIntenet)
//    }
//
//    private fun stopTimer() {
//        stopService(Intent(this, ChunkTimerService::class.java))
//    }
//
//
//    class TimerReceiver(val pomodoroView: PomodoroView) : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            val mTotalTime = intent!!.getLongExtra(
//                ChunkTimerService.EXTRA_TOTAL_TIME,
//                Constants.DEFAULT_TIMER_TIME_MILLIS
//            )
//            val mTimeLeft = intent!!.getLongExtra(ChunkTimerService.TICK_TIME_LEFT, 0)
//            pomodoroView.showTick(mTotalTime.toSeconds(), mTimeLeft.toSeconds())
//        }
//
//    }
//
//
//
//
//}
//
//
//
