package dev.alessi.chunk.pomodoro.timer.android.ui


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.format.DateUtils
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import dev.alessi.chunk.pomodoro.timer.android.AppUtilsProvider
import dev.alessi.chunk.pomodoro.timer.android.ClockView
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.service.ChunkCountDownTimer
import dev.alessi.chunk.pomodoro.timer.android.service.ChunkTimerService
import dev.alessi.chunk.pomodoro.timer.android.service.ChunkTimerService.Command.Companion.ACTION_START_BREAKTIME
import dev.alessi.chunk.pomodoro.timer.android.service.ChunkTimerService.Command.Companion.ACTION_START_TIME_SLICE
import dev.alessi.chunk.pomodoro.timer.android.util.IntentBuilder
import dev.alessi.chunk.pomodoro.timer.android.util.toFormatedElapsedInMinutes
import dev.alessi.chunk.pomodoro.timer.android.util.toFormatedMinutes
import kotlinx.android.synthetic.main.fragment_timer.*


class TimerFragment : Fragment() {
    private lateinit var sizeBtns: List<ClockView>

    private lateinit var mTimerSharedViewModel: TimerSharedViewModel
    private lateinit var mSelectTaskSharedViewModel: SelectTaskSharedViewModel
    private lateinit var mMainActivityControlViewModel: MainActivityControlViewModel
    private lateinit var mAppUtils: AppUtilsProvider
//    private lateinit var mStatusChangedViewModel: StatusChangedViewModel

    private var mSelectedIndex = 2

    private var mSizes: List<Int> = listOf(12, 24, 36, 48, 60)
    private var mGson: Gson = Gson()
    private var mTask: Task = Task(description = "", uid = -1)
    private var mBreaktime = 0
    private var mServiceController: ChunkTimerServiceControl? = null


    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true


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

        activity?.run {
            mTimerSharedViewModel = ViewModelProvider(this)[TimerSharedViewModel::class.java]
            mSelectTaskSharedViewModel = ViewModelProvider(this)[SelectTaskSharedViewModel::class.java]
            mMainActivityControlViewModel = ViewModelProvider(this)[MainActivityControlViewModel::class.java]
            mAppUtils = activity?.application as AppUtilsProvider
        }

        loadPreferences()

        mMainActivityControlViewModel.updateTitle(getString(R.string.app_name))

        initObservers()

    }


    private fun initObservers() {

        mSelectTaskSharedViewModel.getTaskObserver().observe(viewLifecycleOwner, Observer {
            mTask = it

            updateTask()

        })

        mTimerSharedViewModel.breaktime.observe(viewLifecycleOwner, Observer {
            mBreaktime = it
            updateBreaktime()
            storePreferences()

        })

        mTimerSharedViewModel.sizes.observe(viewLifecycleOwner, Observer {
            mSizes = it
            updateSizeButtons()
            resetTimer()
            storePreferences()
        })

        mTimerSharedViewModel.sizeIndex.observe(viewLifecycleOwner, Observer {
            mSelectedIndex = it
            resetTimer()
            storePreferences()
        })


//        mStatusChangedViewModel.onTick.observe(viewLifecycleOwner, Observer {
//            readIntentAndUpdateTimer(it)
//
//        })
    }

    private fun updateSubtitle(type: @ChunkCountDownTimer.Type Int, strCurrentTimer: String, strtPercent: String) {

        val ref = when (type) {
            ChunkCountDownTimer.Type.SLICE_TIMER -> R.string.message_toolbar_subtitle_timer_running
            ChunkCountDownTimer.Type.BREAK_TIMER -> R.string.message_toolbar_subtitle_break_running
            else -> -1
        }


        val str = if (ref == -1) "" else getString(ref, strCurrentTimer, strtPercent)

        mMainActivityControlViewModel.updateSubtitle(str)
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

        forAllSizeButtons { it: ClockView ->
            it.minutes = mSizes[it.tag as Int]
            it.clockSizeName = mAppUtils.getSizeName(it.tag as Int)

        }
    }

    private fun updateBreaktime() {
        val timeSeconds = mBreaktime * 60.toLong()
        val formatBreakTime = DateUtils.formatElapsedTime(timeSeconds)
        txtBreakTimeDisplay.text = formatBreakTime
    }

    private fun resetTimer() {

        forAllSizeButtons { it ->
            it.isChecked = (mSelectedIndex == it.tag)
        }

        val timeMinutes = mSizes[mSelectedIndex]

        val formatedTime = DateUtils.formatElapsedTime(timeMinutes * 60.toLong())
        txtTimerDisplay.text = formatedTime

        val timeMillis = timeMinutes * 60 * 1000.toLong()

        updateTimer(timeMillis, timeMillis, ChunkTimerService.Event.ON_TICK)

        mMainActivityControlViewModel.updateSubtitle("")

    }

    private fun updateTask() {
        txtTask.text = mTask.description
    }

    private fun updateTimer(timeLeft: Long, totalTime: Long, @ChunkCountDownTimer.Type type: Int) {
//        val formatedTime = DateUtils.formatElapsedTime(timeLeft / 1000)
        val formatedTime = timeLeft.toFormatedElapsedInMinutes()

        txtMainTimer.text = formatedTime


        val percFinish: Double = (100 - ((timeLeft.toDouble()) * 100 / totalTime))


        /*if (mTimerRunning)
            updateTimerTextAppereance(timeLeft.toMinutes().toInt())*/


        val xstr = "%.1f".format(percFinish)

        updateSubtitle(type, strCurrentTimer = totalTime.toFormatedMinutes(), strtPercent = xstr)


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
//        btnClearTask.setOnClickListener(::actionClearTask)

        txtTask.movementMethod = ScrollingMovementMethod()


        btnIconSetBreak.setOnClickListener(::openBreaktimeSettingsDialog)
        btnIconSetTaskTimes.setOnClickListener(::openTimerSettingsDialog)

        txtTask.setOnTouchListener(::onTxtTaskTouch)

        btnOpenSettings.setOnClickListener(::openSettingsScreen)

        sizeBtns =
            listOf<ClockView>(btnSizePP, btnSizeP, btnSizeM, btnSizeG, btnSizeGG)


        forAllSizeButtons { _, it ->
            it.setOnClickListener(::onSizeSetupBtnClick)
            val tag = (it.tag as String).toInt()
            it.tag = tag
//            it.setImageResource(getSizeDrawable(tag, context!!))
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
        println("onResume")
        activity?.registerReceiver(
            mTickReceiver,
            IntentFilter(ChunkTimerService.message_broadcast_message)
        )

        mServiceController?.requestStateUpdate()


        super.onResume()
    }


    override fun onPause() {
        println("onPause")

        activity?.unregisterReceiver(mTickReceiver)
        super.onPause()
    }


    private fun onSizeSetupBtnClick(view: View) {
        val tag = (view.tag) as Int
        mTimerSharedViewModel.setSizeIndex(tag)

    }




    private fun showTimerStarted() {
        btnCancelTimer.visibility = View.VISIBLE
        btnCancelTimer.text = getText(R.string.button_label_cancel)

        if (mTask.uid == -1){
            setEmptyTask(R.string.message_hint_no_task)
        }

        onTimerStarted()


    }

    private fun setEmptyTask(resId: Int){
        mSelectTaskSharedViewModel.selectTask(Task(description = getString(resId), uid = -1))
    }

    private fun showBreakTimerStarted() {
        btnCancelTimer.visibility = View.VISIBLE
        btnCancelTimer.text = getText(R.string.button_label_cancel_break)
        setEmptyTask(R.string.label_breaktime)
        onTimerStarted()
    }

    fun showTimerCanceled() { //

        if (mTask.uid == -1) {
            mSelectTaskSharedViewModel.selectTask(
                Task(
                    description = getString(R.string.message_hint_choose_task),
                    uid = -1
                )
            )
        }
        onTimerStopped()

        loadPreferences()

    }


    private fun onTimerStopped() {
        forAllSizeButtons { b ->
            b.isEnabled = true
            activateView(b, b.isEnabled)
        }


        card_task.visibility = View.VISIBLE
        setTaskEnabled(true)

        groupHideOnStart.visibility = View.VISIBLE
        groupShowOnStart.visibility = View.INVISIBLE
    }

    private fun onTimerStarted() {

        forAllSizeButtons { b -> b.isEnabled = false }

        setTaskEnabled(false)

        groupHideOnStart.visibility = View.INVISIBLE
        groupShowOnStart.visibility = View.VISIBLE

        hideUnselectedSizeButtons()
    }

    private fun setTaskEnabled(enabled: Boolean) {
        txtTask.isEnabled = enabled
        activateView(txtTask, enabled)
        txtTask.compoundDrawables.forEach {
            it?.let {
                activateDrawable(it, enabled)
            }
        }


    }

    private fun activateDrawable(drawable: Drawable, activate: Boolean) {

    }

    private fun activateView(view: View, activate: Boolean) {


    }


    private fun hideUnselectedSizeButtons() {
        forAllSizeButtons { btn: ClockView ->
            if (!btn.isChecked) {
                activateView(btn, false)
            }
        }
    }


    private val mTickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            val event = IntentBuilder.getEvent(intent)
            when (event) {
                ChunkTimerService.Event.ON_TICK -> onTick(intent)
                ChunkTimerService.Event.ON_TIMER_STARTED -> onTimeSliceStarted()
                ChunkTimerService.Event.ON_BREAKTIME_STARTED -> onBreaktimeStarted()
                ChunkTimerService.Event.ON_SERVICE_STOPPED -> showTimerCanceled()
            }


        }
    }

    private fun onBreaktimeStarted() {
        showBreakTimerStarted()

    }

    private fun onTimeSliceStarted() {
        showTimerStarted()
    }


    private fun onTick(intent: Intent) {
        val (_, currentTime, totalTime, _, tickType) = IntentBuilder.getServiceExtras(intent.extras!!)

        updateTimer(currentTime, totalTime, tickType!!)


    }


    private fun readIntentAndUpdateTimer(intent: Intent) {
        val timeLeft = intent.getLongExtra(ChunkTimerService.extra_param_current_time, 0)
        val totalTime = intent.getLongExtra(ChunkTimerService.extra_param_total_time_millis, 25)
        val status = intent.getIntExtra(ChunkTimerService.extra_param_event, 0)

        updateTimer(timeLeft, totalTime, status)
    }


    private fun openBreaktimeSettingsDialog(view: View) {
        findNavController().navigate(R.id.inputBreakTimeDialogFragment)
    }

    private fun openSettingsScreen(view: View) {
        findNavController().navigate(R.id.settingsFragment)
    }

    private fun onTxtTaskTouch(v: View, event: MotionEvent): Boolean {
        println(v.javaClass.name)
        println(v.id)
        val tv = v as MaterialTextView

        if (event.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= (tv.right - tv.compoundDrawables[2].bounds.width())) {
                actionClearTask()
            } else {
                openLoadTaskScreen()
            }
            return true
        }

        return false

    }

    private fun openLoadTaskScreen() {
        findNavController().navigate(R.id.selectTaskFragment)

    }

    private fun openTimerSettingsDialog(view: View) {
        findNavController().navigate(R.id.timerSettingsDialogFragment)
    }

    private fun actionClearTask() {
        mSelectTaskSharedViewModel.selectTask(
            Task(
                description = getString(R.string.message_hint_choose_task),
                uid = -1
            )
        )

    }

    private fun actionStartBreaktime(view: View) {
        mServiceController?.doStartService(
            mBreaktime * 60 * 1000.toLong(),
            mSelectedIndex, -1, ACTION_START_BREAKTIME
        )

    }

    private fun actionStartTimer(view: View) {
        val timeMinutes = mSizes[mSelectedIndex]

        mServiceController?.doStartService(
            timeMinutes * 60 * 1000.toLong(),
            mSelectedIndex, mTask.uid!!, ACTION_START_TIME_SLICE
        )

    }

    private fun actionCancelTimer(view: View) {
        mServiceController?.doStopService()

    }

    private fun forAllSizeButtons(action: (Int, ClockView) -> Unit) {
        sizeBtns.forEachIndexed(action)
    }

    private fun forAllSizeButtons(action: (ClockView) -> Unit) {
        sizeBtns.onEach(action)
    }

    private fun forAllCheckedButtons(action: (ClockView) -> Unit) {
        sizeBtns.filter { (mSelectedIndex == it.tag) }.onEach(action)
    }


}


