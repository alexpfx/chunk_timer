package dev.alessi.chunk.pomodoro.timer.android.ui


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.components.BadgedButton
import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.platform.ChunkTimerService
import dev.alessi.chunk.pomodoro.timer.android.ui.dialog.TimerSettingsDialogFragment
import dev.alessi.chunk.pomodoro.timer.android.util.Command
import dev.alessi.chunk.pomodoro.timer.android.util.Command.Companion.ACTION_START_BREAK
import dev.alessi.chunk.pomodoro.timer.android.util.Command.Companion.ACTION_START_TIMER
import dev.alessi.chunk.pomodoro.timer.android.util.Command.Companion.ACTION_STOP
import dev.alessi.chunk.pomodoro.timer.android.util.Command.Companion.ACTION_TICK
import dev.alessi.chunk.pomodoro.timer.android.util.Command.Companion.ACTION_UPDATE_STATE
import dev.alessi.chunk.pomodoro.timer.android.util.IntentBuilder
import kotlinx.android.synthetic.main.fragment_timer.*


class TimerFragment : Fragment() {


    private var TAG = TimerFragment::class.java.name
    private lateinit var sizeBtns: List<BadgedButton>


    private var mTimerRunning = false
    private lateinit var mTimerSharedViewModel: TimerSharedViewModel
    private lateinit var mSelectTaskSharedViewModel: SelectTaskSharedViewModel
    private lateinit var mainSharedViewModel: MainSharedViewModel

    private var mSelectedIndex = 2

    private var mSizes: List<Int> = listOf(12, 24, 36, 48, 60)
    private var mGson: Gson = Gson()
    private var mTask: Task = Task(description = "", uid = -1)
    private var mBreaktime = 0
    private var mServiceController: ChunkTimerServiceControl? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        retainInstance = true
        super.onCreate(savedInstanceState)
    }


    companion object {
        const val DEFAULT_TIME_BREAK = 10
        const val DEFAULT_JSON_SIZES = "[18,24,36,48,60]"
        const val KEY_SIZE_JSON_ARRAY = "KEY_SIZE_JSON_ARRAY"
        const val KEY_LAST_INDEX = "key_last_index"
        const val KEY_LAST_BREAKTIME = "KEY_LAST_BREAKTIME"
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        mTimerSharedViewModel = activity?.run {
            ViewModelProviders.of(this)[TimerSharedViewModel::class.java]
        } ?: throw IllegalStateException("Invalid activity")

        mSelectTaskSharedViewModel = activity?.run {
            ViewModelProviders.of(this)[SelectTaskSharedViewModel::class.java]
        } ?: throw IllegalStateException("Invalid activity")

        activity?.run {
            mainSharedViewModel = ViewModelProviders.of(this).get(MainSharedViewModel::class.java)
        } ?: throw Throwable("invalid activity")

        loadPreferences()

        mainSharedViewModel.updateTitle(getString(R.string.app_name))
    }

    override fun onStop() {

        mTimerSharedViewModel.breaktime.removeObservers(viewLifecycleOwner)
        mTimerSharedViewModel.sizeIndex.removeObservers(viewLifecycleOwner)
        mTimerSharedViewModel.sizes.removeObservers(viewLifecycleOwner)

        super.onStop()
    }

    override fun onStart() {
        super.onStart()

        mSelectTaskSharedViewModel.getTaskObserver().observe(viewLifecycleOwner, Observer {
            mTask = it

            updateTask()

        })

        mTimerSharedViewModel.breaktime.observe(viewLifecycleOwner, Observer {
            mBreaktime = it
            updateBreaktime()
            storePreferences()

        })

        mTimerSharedViewModel.sizeIndex.observe(viewLifecycleOwner, Observer {
            mSelectedIndex = it
            resetTimer()
            storePreferences()
        })

        mTimerSharedViewModel.sizes.observe(viewLifecycleOwner, Observer {
            mSizes = it
            updateSizeButtons()
            resetTimer()
            storePreferences()
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
        mTimerSharedViewModel.setSizes(sizesArray.toList())

        val index = sharedPreferences.getInt(KEY_LAST_INDEX, mSelectedIndex)
        mTimerSharedViewModel.setSizeIndex(index)


        val lastBreaktime = sharedPreferences.getInt(KEY_LAST_BREAKTIME, 10)
        mTimerSharedViewModel.setBreaktime(lastBreaktime)

    }

    private fun updateSizeButtons() {

        forAllSizeButtons { it: BadgedButton ->
            val intTag = (it.tag as String).toInt()
            it.text = "${mSizes[intTag]} min"
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

    private fun updateTask() {

        txtTask.text = mTask.description
    }

    private fun updateTimer(timeLeft: Long, totalTime: Long) {
        val formatedTime = DateUtils.formatElapsedTime(timeLeft / 1000)
        txtMainTimer.text = formatedTime

        val percFinish: Double = (100 - ((timeLeft.toDouble()) * 100 / totalTime))

        val xstr = "%.1f".format(percFinish)

//        txtPercent.text = "$xstr %"
//        separator_view.progress = percFinish.toInt()


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
        btnClearTask.setOnClickListener(::actionClearTask)


        btnIconSetBreak.setOnClickListener(::openBreaktimeSettingsDialog)
        btnIconSetTaskTimes.setOnClickListener(::openTimerSettingsDialog)
        txtTask.setOnClickListener(::openLoadTaskScreen)
        btnOpenSettings.setOnClickListener(::openSettingsScreen)

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

        if (mTimerRunning) {
            mServiceController?.requestStateUpdate()
        }

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
        mTimerSharedViewModel.setSizeIndex(tag)
        updateColor(view)
    }

    fun createGradientDrawable(colorRes: Int, strokeColorRes: Int): GradientDrawable {
        val g = GradientDrawable()
        val context = this.context!!
        g.setColor(ContextCompat.getColor(context, colorRes))
        g.cornerRadius = 16.0f
        g.setStroke(1, ContextCompat.getColor(context, strokeColorRes))

        return g
    }

    private fun updateColor(view: View) {

        forAllSizeButtons { frame ->
            frame.isChecked = (frame.tag as String).toInt() == mSelectedIndex
        }


//        view.setBackgroundColor(ContextCompat.getColor(context!!,R.color.color_pizza_g))
//        (view.parent as View).setBackgroundColor(ContextCompat.getColor(context!!,R.color.colorPrimary44))


//        frTxtMainTimer.background = background
        /*scrollviewBase.background = background
        ((scrollviewBase.parent) as View).background = background

        linear_layout_buttons_base.background = background*/
    }


    fun showTimerStarted() {
        btnCancelTimer.visibility = View.VISIBLE
        btnCancelTimer.text = getText(R.string.button_label_cancel)
        val txtTaskText =
            if (mTask.description.isEmpty()) getString(R.string.message_hint_no_task) else mTask.description
        txtTask.setText(txtTaskText)

        disableViews()

    }

    fun showBreakTimerStarted() {
        btnCancelTimer.visibility = View.VISIBLE
        btnCancelTimer.text = getText(R.string.button_label_cancel_break)

        txtTask.setText(getString(R.string.label_breaktime))

        disableViews()
    }

    fun showTimerCanceled(intent: Intent) { //
        btnCancelTimer.visibility = View.INVISIBLE

        if (mTask.description.isEmpty()) {
            mSelectTaskSharedViewModel.selectTask(
                Task(
                    description = getString(R.string.message_hint_choose_task),
                    uid = -1
                )
            )
        }
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

        btnOpenSettings.visibility = View.VISIBLE
        txtTask.isEnabled = true
        btnClearTask.visibility = View.VISIBLE

    }


    private fun disableViews() {
        btnStartTimer.visibility = View.INVISIBLE
        btnStartBreak.visibility = View.INVISIBLE
        forAllSizeButtons { b -> b.isEnabled = false }
        layoutTimerSettings.visibility = View.GONE

        btnOpenSettings.visibility = View.INVISIBLE
        txtTask.isEnabled = false
        btnClearTask.visibility = View.INVISIBLE
    }


    private val mTickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            when (@Command IntentBuilder.getCommand(
                intent
            )) {
                ACTION_STOP -> {
                    showTimerCanceled(intent)

                    val index = intent.getIntExtra(ChunkTimerService.extra_param_last_index, 2)
                    mTimerSharedViewModel.setSizeIndex(index)

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

        updateTimer(timeLeft, totalTime)
    }


    private fun openBreaktimeSettingsDialog(view: View) {
        findNavController().navigate(R.id.inputBreakTimeDialogFragment)
    }

    private fun openSettingsScreen(view: View) {
        findNavController().navigate(R.id.settingsFragment)
    }

    private fun openLoadTaskScreen(view: View) {
        findNavController().navigate(R.id.taskFragment)
    }

    private fun openTimerSettingsDialog(view: View) {
        findNavController().navigate(R.id.timerSettingsDialogFragment)

        TimerSettingsDialogFragment().show(
            activity!!.supportFragmentManager,
            "TimerSettingsDialogFragment"
        )

    }

    private fun actionClearTask(view: View) {
        mSelectTaskSharedViewModel.selectTask(
            Task(
                description = getString(R.string.message_hint_choose_task),
                uid = -1
            )
        )
    }

    private fun actionStartBreaktime(view: View) {
        mTimerRunning = true
        mServiceController?.doStartService(
            mBreaktime * 60 * 1000.toLong(),
            mSelectedIndex, -1, Command.ACTION_START_BREAK
        )
    }

    private fun actionStartTimer(view: View) {
        mTimerRunning = true
        val timeMinutes = mSizes[mSelectedIndex]

        mServiceController?.doStartService(
            timeMinutes * 60 * 1000.toLong(),
            mSelectedIndex, mTask.uid!!, ACTION_START_TIMER
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


