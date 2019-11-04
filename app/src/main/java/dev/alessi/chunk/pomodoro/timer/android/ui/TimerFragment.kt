package dev.alessi.chunk.pomodoro.timer.android.ui


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.components.BadgedButton
import dev.alessi.chunk.pomodoro.timer.android.platform.ChunkTimerService
import dev.alessi.chunk.pomodoro.timer.android.platform.SoundEffectManager
import dev.alessi.chunk.pomodoro.timer.android.ui.dialog.TimerSettingsDialogFragment
import dev.alessi.chunk.pomodoro.timer.android.util.Command
import dev.alessi.chunk.pomodoro.timer.android.util.Command.Companion.ACTION_START_BREAK
import dev.alessi.chunk.pomodoro.timer.android.util.Command.Companion.ACTION_START_TIMER
import dev.alessi.chunk.pomodoro.timer.android.util.Command.Companion.ACTION_STOP
import dev.alessi.chunk.pomodoro.timer.android.util.Command.Companion.ACTION_TICK
import dev.alessi.chunk.pomodoro.timer.android.util.Command.Companion.ACTION_UPDATE_STATE
import dev.alessi.chunk.pomodoro.timer.android.util.IntentBuilder
import dev.alessi.chunk.pomodoro.timer.android.util.debug
import kotlinx.android.synthetic.main.fragment_timer.*


class TimerFragment : Fragment() {

    private lateinit var mediaPlayer: SoundEffectManager

    private var TAG = TimerFragment::class.java.name
    private lateinit var sizeBtns: List<BadgedButton>

    private var mTimerRunning = false
    private lateinit var mSharedViewModel: SharedViewModel
    private var mSelectedIndex = 2

    private var mSizes: List<Int> = listOf(12, 24, 36, 48, 60)
    private var mGson: Gson = Gson()
    private var mTask = ""
    private var mBreaktime = 0
    private var mServiceController: ChunkTimerServiceControl? = null


    companion object {
        const val DEFAULT_TIME_BREAK = 10
        const val DEFAULT_JSON_SIZES = "[18,24,36,48,60]"
        const val KEY_SIZE_JSON_ARRAY = "KEY_SIZE_JSON_ARRAY"
        const val KEY_LAST_INDEX = "key_last_index"
        const val KEY_LAST_BREAKTIME = "KEY_LAST_BREAKTIME"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mediaPlayer =
            SoundEffectManager(context = this.context!!)

        mSharedViewModel = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw IllegalStateException("Invalid activity")

        loadPreferences()


        mSharedViewModel.breaktime.observe(this, Observer {
            mBreaktime = it
            updateBreaktime()
            storePreferences()

        })


        mSharedViewModel.sizeIndex.observe(this, Observer {
            mSelectedIndex = it
            resetTimer()
            storePreferences()
        })

        mSharedViewModel.sizes.observe(this, Observer {
            mSizes = it
            updateSizeButtons()
            resetTimer()
            storePreferences()
        })
        mSharedViewModel.taskname.observe(this, Observer {
            mTask = it
        })


    }

    private fun storePreferences() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context)
        val sizesJsonStr = mGson.toJson(mSizes)

        sharedPreferences.edit()
            .putInt(KEY_LAST_INDEX, mSelectedIndex).putInt(KEY_LAST_BREAKTIME, mBreaktime)
            .putString(
                KEY_SIZE_JSON_ARRAY, sizesJsonStr
            ).apply()

    }

    private fun loadPreferences() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context)
        val sizesStr = sharedPreferences.getString(KEY_SIZE_JSON_ARRAY, DEFAULT_JSON_SIZES)
        val sizesArray = mGson.fromJson(sizesStr, Array<Int>::class.java)
        mSharedViewModel.setSizes(sizesArray.toList())

        val index = sharedPreferences.getInt(KEY_LAST_INDEX, mSelectedIndex)
        mSharedViewModel.setSizeIndex(index)


        val lastBreaktime = sharedPreferences.getInt(KEY_LAST_BREAKTIME, 10)
        mSharedViewModel.setBreaktime(lastBreaktime)

    }

    override fun onDestroy() {

        mediaPlayer.dispose()
        super.onDestroy()

    }

    private fun updateSizeButtons() {
        forAllSizeButtons { it ->
            val intTag = (it.tag as String).toInt()
            it.badgeText = mSizes[intTag].toString()
        }
    }

    private fun updateBreaktime() {
        val timeSeconds = mBreaktime * 60.toLong()
        val formatBreakTime = DateUtils.formatElapsedTime(timeSeconds)
        txtBreakTimeDisplay.text = formatBreakTime
    }

    private fun resetTimer() {

        forAllSizeButtons { it ->
            val intTag = (it.tag as String).toInt()
            it.isChecked = (mSelectedIndex == intTag)
        }

        val timeMinutes = mSizes[mSelectedIndex]

        val formatedTime = DateUtils.formatElapsedTime(timeMinutes * 60.toLong())
        txtTimerDisplay.text = formatedTime

        val timeMillis = timeMinutes * 60 * 1000.toLong()

        updateTimer(timeMillis, timeMillis)

    }

    private fun updateTimer(timeLeft: Long, totalTime: Long) {
        val formatedTime = DateUtils.formatElapsedTime(timeLeft / 1000)
        txtMainTimer.text = formatedTime

        val percFinish: Double = (100 - ((timeLeft.toDouble()) * 100 / totalTime))

        val xstr = "%.1f".format(percFinish)

        txtPercent.text = "$xstr %"
        progressBar.progress = percFinish.toInt()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        btnStartTimer.setOnClickListener(::actionStartTimer)
        btnCancelTimer.setOnClickListener(::actionCancelTimer)
        btnStartBreak.setOnClickListener(::actionStartBreaktime)

        edtTask.addTextChangedListener {
            mTask = it.toString()
        }

        btnIconSetBreak.setOnClickListener(::openBreaktimeSettingsDialog)
        btnIconSetTaskTimes.setOnClickListener(::openTimerSettingsDialog)
        btnLoadTask.setOnClickListener(::openLoadTaskScreen)
        btnLoadSettings.setOnClickListener (::openSettingsScreen)


        sizeBtns =
            listOf<BadgedButton>(frBtnSizePP, frBtnSizeP, frBtnSizeM, frBtnSizeG, frBtnSizeGG)

        forAllSizeButtons { _, it ->
            it.setOnClickListener(::onSizeSetupBtnClick)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mServiceController = context as ChunkTimerServiceControl

        (context as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

    }

    override fun onDetach() {
        mServiceController = null
        super.onDetach()
    }


    override fun onResume() {
        activity?.registerReceiver(
            mTickReceiver,
            IntentFilter(ChunkTimerService.message_broadcast_message)
        )

        mServiceController?.requestStateUpdate()

        super.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("isRunning", mTimerRunning)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            mTimerRunning = savedInstanceState.getBoolean("isRunning")
        }
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        activity?.unregisterReceiver(mTickReceiver)
        super.onPause()
    }


    private fun onSizeSetupBtnClick(view: View) {
        val tag = (view.tag as String).toInt()
        mSharedViewModel.setSizeIndex(tag)
    }


    fun showTimerStarted() {
        btnCancelTimer.visibility = View.VISIBLE
        btnCancelTimer.text = getText(R.string.button_label_cancel)

        disableViews()

    }

    fun showBreakTimerStarted() {
        btnCancelTimer.visibility = View.VISIBLE
        btnCancelTimer.text = getText(R.string.button_label_cancel_break)

        disableViews()
    }

    fun showTimerCanceled(intent: Intent) { //
        btnCancelTimer.visibility = View.INVISIBLE

        enableViews()

    }


    fun showBreakTimerCanceled(intent: Intent) {
        val timeLeft = intent.getLongExtra(ChunkTimerService.extra_param_current_time, 0)
        val totalTime = intent.getLongExtra(ChunkTimerService.extra_param_total_time_millis, 5)

        btnCancelTimer.visibility = View.INVISIBLE

        enableViews()

        updateTimer(timeLeft, totalTime)

    }


    private fun enableViews() {
        btnStartTimer.visibility = View.VISIBLE
        btnStartBreak.visibility = View.VISIBLE
        forAllSizeButtons { b -> b.isEnabled = true }
        layoutTimerSettings.visibility = View.VISIBLE
        edtTask.isEnabled = true
        btnLoadTask.visibility = View.VISIBLE
    }


    private fun disableViews() {
        btnStartTimer.visibility = View.INVISIBLE
        btnStartBreak.visibility = View.INVISIBLE
        forAllSizeButtons { b -> b.isEnabled = false }
        layoutTimerSettings.visibility = View.GONE
        edtTask.isEnabled = false
        btnLoadTask.visibility = View.INVISIBLE
    }


    private val mTickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            when (@Command IntentBuilder.getCommand(
                intent
            )) {
                ACTION_STOP -> {
                    showTimerCanceled(intent)

                    val index = intent.getIntExtra(ChunkTimerService.extra_param_last_index, 2)
                    mSharedViewModel.setSizeIndex(index)

                }
                ACTION_TICK -> showTick(intent)
                ACTION_START_TIMER -> showTimerStarted()
                ACTION_UPDATE_STATE -> updateState(intent)
                ACTION_START_BREAK -> showBreakTimerStarted()
            }

        }
    }

    private fun updateState(intent: Intent) {
        val timeLeft = intent.getLongExtra(ChunkTimerService.extra_param_current_time, 0)
        val totalTime = intent.getLongExtra(ChunkTimerService.extra_param_total_time_millis, 25)
        val silent = intent.getBooleanExtra(ChunkTimerService.extra_param_no_sound_on_tick, false)
        val status = intent.getIntExtra(ChunkTimerService.extra_param_status, 0)

        mTimerRunning = status != ChunkTimerService.TimerState.status_ready

        updateTimer(timeLeft, totalTime)
        showBreakTimerCanceled(intent)
        showTimerCanceled(intent)

        when (status) {
            ChunkTimerService.TimerState.status_ready -> {


            }
            ChunkTimerService.TimerState.status_running_timer -> {
                showTimerStarted()

            }
            ChunkTimerService.TimerState.status_running_break -> {
                showBreakTimerStarted()
            }

        }


    }

    fun showTick(intent: Intent) {

        val timeLeft = intent.getLongExtra(ChunkTimerService.extra_param_current_time, 0)
        val totalTime = intent.getLongExtra(ChunkTimerService.extra_param_total_time_millis, 25)
        val silent = intent.getBooleanExtra(ChunkTimerService.extra_param_no_sound_on_tick, false)

        debug("silent: $silent")

        if (!silent) {
            mediaPlayer.playTick()
        }

        updateTimer(timeLeft, totalTime)
    }


    private fun openBreaktimeSettingsDialog(view: View) {
        findNavController().navigate(R.id.inputBreakTimeDialogFragment)
    }

    private fun openSettingsScreen(view: View) {
        findNavController().navigate(R.id.settingsFragment)



    }

    private fun openLoadTaskScreen(view: View) {

    }

    private fun openTimerSettingsDialog(view: View) {
        TimerSettingsDialogFragment().show(
            activity!!.supportFragmentManager,
            "TimerSettingsDialogFragment"
        )

    }



    private fun actionStartBreaktime(view: View) {
        mTimerRunning = true
        mServiceController?.doStartService(
            mBreaktime * 60 * 1000.toLong(),
            mSelectedIndex, mTask, Command.ACTION_START_BREAK
        )
    }

    private fun actionStartTimer(view: View) {
        mTimerRunning = true
        val timeMinutes = mSizes[mSelectedIndex]

        mServiceController?.doStartService(
            timeMinutes * 60 * 1000.toLong(),
            mSelectedIndex, mTask, Command.ACTION_START_TIMER
        )

    }


    private fun actionCancelTimer(view: View) {
        mTimerRunning = false
        mServiceController?.doStopService()

    }


    private fun forAllSizeButtons(action: (Int, BadgedButton) -> Unit) {
        sizeBtns.forEachIndexed(action)
    }

    private fun forAllSizeButtons(action: (BadgedButton) -> Unit) {
        sizeBtns.onEach(action)
    }

}


