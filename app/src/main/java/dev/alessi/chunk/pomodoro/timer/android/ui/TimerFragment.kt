package dev.alessi.chunk.pomodoro.timer.android.ui


import android.content.*
import android.os.Bundle
import android.text.format.DateUtils
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.arraySetOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.gson.Gson
import dev.alessi.chunk.pomodoro.timer.android.AppUtilsProvider
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.components.customview.ClockView
import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.service.ChunkCountDownTimer
import dev.alessi.chunk.pomodoro.timer.android.service.ChunkTimerService
import dev.alessi.chunk.pomodoro.timer.android.service.ChunkTimerService.Command.Companion.ACTION_START_BREAKTIME
import dev.alessi.chunk.pomodoro.timer.android.service.ChunkTimerService.Command.Companion.ACTION_START_TIME_SLICE
import dev.alessi.chunk.pomodoro.timer.android.ui.TimerLayoutEventViewModel.TimerLayoutEventType.Companion.BREAKTIME_PAGE_SELECTED
import dev.alessi.chunk.pomodoro.timer.android.ui.TimerLayoutEventViewModel.TimerLayoutEventType.Companion.CLOCK_TIMER_SETUP
import dev.alessi.chunk.pomodoro.timer.android.ui.TimerLayoutEventViewModel.TimerLayoutEventType.Companion.INDEX_CHANGE
import dev.alessi.chunk.pomodoro.timer.android.ui.TimerLayoutEventViewModel.TimerLayoutEventType.Companion.TASK_CLEARED
import dev.alessi.chunk.pomodoro.timer.android.ui.TimerLayoutEventViewModel.TimerLayoutEventType.Companion.TASK_SELECTED
import dev.alessi.chunk.pomodoro.timer.android.ui.TimerLayoutEventViewModel.TimerLayoutEventType.Companion.TIMER_PAGE_SELECTED
import dev.alessi.chunk.pomodoro.timer.android.ui.TimerLayoutEventViewModel.TimerLayoutEventType.Companion.TIMER_STARTED
import dev.alessi.chunk.pomodoro.timer.android.ui.TimerLayoutEventViewModel.TimerLayoutEventType.Companion.TIMER_STOPPED
import dev.alessi.chunk.pomodoro.timer.android.util.IntentBuilder
import dev.alessi.chunk.pomodoro.timer.android.util.toFormatedElapsedInMinutes
import dev.alessi.chunk.pomodoro.timer.android.util.toFormatedMinutes
import kotlinx.android.synthetic.main.fragment_timer.*


class TimerFragment : Fragment() {
    private val mConfigTimersSharedViewModel: ConfigTimersSharedViewModel by viewModels(::requireActivity)
    private val mSelectTaskSharedViewModel: SelectTaskSharedViewModel by viewModels(::requireActivity)
    private val mMainActivityControlSharedViewModel: MainActivityControlViewModel by viewModels(::requireActivity)

    private val mLayoutEventViewModel: TimerLayoutEventViewModel by viewModels()

    private lateinit var mAppUtils: AppUtilsProvider
//    private lateinit var mStatusChangedViewModel: StatusChangedViewModel

//    private var mSelectedIndex = 2
//    private var mSelectedBrektimeIndex = 0

    private var mLayoutStatus = TimerLayoutEventViewModel.TimerFragmentViewState(

    )


    private var mGson: Gson = Gson()
    private var mTask: Task = Task(name = "", description = "", uid = -1)

    private var mServiceController: ChunkTimerServiceControl? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true
    }

    companion object {
        const val DEFAULT_TIME_BREAK = 10
        const val DEFAULT_JSON_SIZES = "[18,24,36,48,60]"
        const val DEFAULT_JSON_BREAKS = "[5,10,20]"
        const val KEY_SIZE_JSON_ARRAY = "KEY_SIZE_JSON_ARRAY"
        const val KEY_BREAKTIME_JSON_ARRAY = "KEY_BREAKTIME_JSON_ARRAY"
        const val KEY_LAST_INDEX = "key_last_index"
        const val KEY_LAST_BREAKTIME_INDEX = "KEY_LAST_BREAKTIME_INDEX"
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mAppUtils = activity?.application as AppUtilsProvider

        initViewModelsObservers()
        loadPreferences()

    }

    private fun handleServiceEvent(intent: Intent) {
        when (IntentBuilder.getEvent(intent)) {
            ChunkTimerService.Event.ON_TICK -> onTick(intent)
            ChunkTimerService.Event.ON_TIMER_STARTED -> eventTimerStarted()
            ChunkTimerService.Event.ON_BREAKTIME_STARTED -> eventTimerStarted()
            ChunkTimerService.Event.ON_SERVICE_STOPPED -> onTimerCanceled()
        }
    }

    private fun handleLayoutEvent(event: TimerLayoutEventViewModel.LayoutEvent) {
        /*se controlId == 0, não deve ignorar, pois layoutStatus é indefinido. */
        if (mLayoutStatus.controlId > 0 && mLayoutStatus == event.newStatus) {
            return
        }

        mLayoutStatus = event.newStatus.copy(controlId = 1)

        updateLayout(event.eventType)
    }

    private fun updateLayout(eventType: Int) {
        when (eventType) {
            TIMER_STARTED, TIMER_STOPPED -> updateTimerRelatedViews()
            TIMER_PAGE_SELECTED, BREAKTIME_PAGE_SELECTED -> {
                updateTimerTypeRelatedViews()
                updateTaskRelatedViews()

            }
            TASK_SELECTED, TASK_CLEARED -> updateTaskRelatedViews()
            CLOCK_TIMER_SETUP -> {
                setupClock()
            }
            INDEX_CHANGE -> {
                storeLastIndexes()
                setupClock()
            }
        }

    }

    /**
     * Método que configura o relógio quando um evento de atualização de status
     * do tipo CLOCK_TIMER_SET acontece.
     */
    private fun setupClock() {
        clockviewgroup_chunk_timers.updateSizes(mLayoutStatus.chunkSizes)
        clockviewgroup_breaktimes.updateSizes(mLayoutStatus.breaktimeSizes)

        clockviewgroup_chunk_timers.syncSelection(mLayoutStatus.chunkIndex)
        clockviewgroup_breaktimes.syncSelection(mLayoutStatus.breaktimeIndex)

        val timeMinutes = getTimerMinutes()

        val formatedTime = DateUtils.formatElapsedTime(timeMinutes * 60.toLong())
//        txtTimerDisplay.text = formatedTime

        val timeMillis = timeMinutes * 60 * 1000.toLong()

        updateClockTimer(timeMillis, timeMillis, -1)
//        mMainActivityControlSharedViewModel.updateSubtitle("")


    }


    private fun updateTaskRelatedViews() {
        setViewVisibility(group_task, mLayoutStatus.showGroupTask)
        setViewVisibility(txt_task_empty, mLayoutStatus.showTxtTaskEmpty)
    }

    private fun updateTimerTypeRelatedViews() {
        setViewVisibility(clockviewgroup_chunk_timers, mLayoutStatus.isTimer, clockviewgroup_breaktimes)
        txtTimerTitle.setText(mLayoutStatus.txtTimerTitleRes)
    }

    private fun updateTimerRelatedViews() {
        setViewVisibility(btn_start_timer, mLayoutStatus.showStartTimerButton, btn_cancel_timer)
        enableAll(!mLayoutStatus.readOnly)
    }


    private fun setViewVisibility(view: View, visible: Boolean, opositeView: View? = null) {
        view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        opositeView?.visibility = if (visible) View.INVISIBLE else View.VISIBLE

        view.requestLayout()
    }


    private fun showGroupTask() = mTask.uid != -1

    private fun initViewModelsObservers() {

        mLayoutEventViewModel.onEventFiredObserver.observe(viewLifecycleOwner, Observer {
            handleLayoutEvent(it)
        })

        mSelectTaskSharedViewModel.onTaskSelectedObserver().observe(viewLifecycleOwner, Observer {
            mTask = it

            updateTask()

            mLayoutEventViewModel.fireEvent(
                TASK_SELECTED,
                mLayoutStatus.copy(showGroupTask = showGroupTask(), showTxtTaskEmpty = !showGroupTask())
            )

        })

        mConfigTimersSharedViewModel.run {

            onChunkSizesChangedObserver.observe(viewLifecycleOwner, Observer {
                mLayoutEventViewModel.fireEvent(CLOCK_TIMER_SETUP, mLayoutStatus.copy(chunkSizes = it))


            })

            onBreaktimesChangedObserver.observe(viewLifecycleOwner, Observer {
                mLayoutEventViewModel.fireEvent(CLOCK_TIMER_SETUP, mLayoutStatus.copy(breaktimeSizes = it))
            })

        }

    }

    private fun updateSubtitle(type: @ChunkCountDownTimer.Type Int, strCurrentTimer: String, strtPercent: String) {

        val ref = when (type) {
            ChunkCountDownTimer.Type.SLICE_TIMER -> R.string.message_toolbar_subtitle_timer_running
            ChunkCountDownTimer.Type.BREAK_TIMER -> R.string.message_toolbar_subtitle_break_running
            else -> -1
        }

        val str = if (ref == -1) "" else getString(ref, strCurrentTimer, strtPercent)

        mMainActivityControlSharedViewModel.updateSubtitle(str)
    }


    private fun storeLastIndexes() {
        getSharedPreferences().edit()
            .putInt(KEY_LAST_INDEX, mLayoutStatus.chunkIndex)
            .putInt(KEY_LAST_BREAKTIME_INDEX, mLayoutStatus.breaktimeIndex)
            .apply()

    }

    private fun loadPreferences() {
        val sharedPreferences = getSharedPreferences()
        val chunktimesStr = sharedPreferences.getString(KEY_SIZE_JSON_ARRAY, DEFAULT_JSON_SIZES)
        val breaktimeStr = sharedPreferences.getString(KEY_BREAKTIME_JSON_ARRAY, DEFAULT_JSON_BREAKS)

        val chunktimesList = mGson.fromJson(chunktimesStr, Array<Int>::class.java).toList()
        val breaktimes = mGson.fromJson(breaktimeStr, Array<Int>::class.java).toList()

        val breaktimeIndex = sharedPreferences.getInt(KEY_LAST_BREAKTIME_INDEX, 0)
        val chunktimeIndex = sharedPreferences.getInt(KEY_LAST_INDEX, 2)

        mLayoutEventViewModel.fireEvent(CLOCK_TIMER_SETUP, mLayoutStatus.copy(chunkSizes = chunktimesList, breaktimeSizes = breaktimes, chunkIndex = chunktimeIndex, breaktimeIndex = breaktimeIndex))


    }

    private fun getSharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    /*private fun updateChunkSizeClockviews() {
        clockviewgroup_chunk_timers.updateSizes(mChunkSizes)
        clockviewgroup_chunk_timers.syncSelection(mLayoutStatus.sizeIndex)

    }

    private fun updateBreaktimesSizeClockViews() {
        clockviewgroup_breaktimes.updateSizes(mBreaktimes)
        clockviewgroup_chunk_timers.syncSelection(mLayoutStatus.breakIndex)

    }
*/

    private fun updateTask() {
        txt_task_name.text = mTask.name
        txt_task_desc.text = mTask.description
    }

    private fun updateClockTimer(timeLeft: Long, totalTime: Long, @ChunkCountDownTimer.Type type: Int) {
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


    private val onTimerModeCheckedListener = MaterialButtonToggleGroup.OnButtonCheckedListener { group, checkedId, isChecked ->
        if (!isChecked) {
            return@OnButtonCheckedListener
        }

        when (checkedId) {
            R.id.toggle_button_timer -> {
                mLayoutEventViewModel.fireEvent(
                    TIMER_PAGE_SELECTED,
                    mLayoutStatus.copy(
                        isTimer = true,
                        showGroupTask = showGroupTask(),
                        showTxtTaskEmpty = !showGroupTask(),
                        txtTimerTitleRes = R.string.label_timer_
                    )
                )

                mLayoutEventViewModel.fireEvent(CLOCK_TIMER_SETUP, mLayoutStatus.copyAndIncId())

            }

            R.id.toggle_button_break -> {
                mLayoutEventViewModel.fireEvent(
                    BREAKTIME_PAGE_SELECTED,
                    mLayoutStatus.copy(
                        isTimer = false,
                        showGroupTask = false, showTxtTaskEmpty = false, txtTimerTitleRes = R.string.label_breaktime_
                    )

                )
                mLayoutEventViewModel.fireEvent(CLOCK_TIMER_SETUP, mLayoutStatus.copyAndIncId())
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initEventListeners()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initEventListeners() {
        btn_start_timer.setOnClickListener(::onClickStartTimer)

        toggle_group_timer_break.addOnButtonCheckedListener(onTimerModeCheckedListener)

        clockviewgroup_chunk_timers.onClockViewSelected = onClockViewSelected
        clockviewgroup_chunk_timers.onViewMoreButtonClick = onViewMoreButtonClick

        clockviewgroup_breaktimes.onClockViewSelected = onClockViewBreakSelected
        clockviewgroup_breaktimes.onViewMoreButtonClick = onViewMoreBreakButtonClick

        btn_cancel_timer.setOnClickListener(::actionCancelTimer)

        btn_clear_task.setOnClickListener(::onClickClearTask)

        txt_task_name.movementMethod = ScrollingMovementMethod()

        arraySetOf<TextView>(txt_task_empty, txt_task_desc, txt_task_name).forEach {
            it.setOnClickListener(::onClickLoadTask)
        }

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
        mMainActivityControlSharedViewModel.updateTitle(getString(R.string.app_name))

        activity?.registerReceiver(
            mServiceEventBroadcastReciver,
            IntentFilter(ChunkTimerService.message_broadcast_message)
        )

        mServiceController?.requestStateUpdate()

        super.onResume()
    }

    override fun onPause() {
        activity?.unregisterReceiver(mServiceEventBroadcastReciver)
        super.onPause()
    }

    private val onViewMoreButtonClick = { view: View ->
        openTimerSettingsDialog(view)
    }

    private val onClockViewSelected = { clockView: ClockView ->
        val tag = (clockView.tag).toString()

        mLayoutEventViewModel.fireEvent(INDEX_CHANGE, mLayoutStatus.copy(chunkIndex = tag.toInt()))
    }

    private val onClockViewBreakSelected = { clockView: ClockView ->
        val tag = (clockView.tag).toString()
        mLayoutEventViewModel.fireEvent(INDEX_CHANGE, mLayoutStatus.copy(breaktimeIndex = tag.toInt()))

    }

    private val onViewMoreBreakButtonClick = { view: View ->
        openBreaktimeSettingsDialog(view)
    }

    private fun onTimerCanceled() { //
        mLayoutEventViewModel.fireEvent(
            TIMER_STOPPED,
            mLayoutStatus.copy(showStartTimerButton = true, readOnly = false)
        )

        mLayoutEventViewModel.fireEvent(CLOCK_TIMER_SETUP, mLayoutStatus.copy())

        setupClock()
    }

    private fun eventTimerStarted() {
        mLayoutEventViewModel.fireEvent(
            TIMER_STARTED,
            mLayoutStatus.copy(
                showStartTimerButton = false,
                readOnly = true
            )
        )
    }

    private fun enableAll(enabled: Boolean) {
        txt_task_name.isEnabled = enabled
        txt_task_desc.isEnabled = enabled
        txt_task_empty.isEnabled = enabled
        clockviewgroup_breaktimes.isEnabled = enabled
        clockviewgroup_chunk_timers.isEnabled = enabled
        toggle_button_timer.isEnabled = enabled
        toggle_button_break.isEnabled = enabled

    }

    private val mServiceEventBroadcastReciver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            handleServiceEvent(intent)

        }
    }

    private fun onTick(intent: Intent) {
        val (_, currentTime, totalTime, _, tickType) = IntentBuilder.getServiceExtras(intent.extras!!)

        updateClockTimer(currentTime, totalTime, tickType!!)
    }

    private fun openBreaktimeSettingsDialog(view: View) {
        findNavController().navigate(R.id.inputBreakTimeDialogFragment)
    }


    private fun onClickClearTask(v: View) {
        actionClearTask()
    }

    private fun onClickLoadTask(v: View) {
        actionOpenLoadTaskScreen()
    }


    private fun actionOpenLoadTaskScreen() {
        findNavController().navigate(R.id.selectTaskFragment)

    }

    private fun openTimerSettingsDialog(view: View) {
        findNavController().navigate(R.id.timerSettingsDialogFragment)
    }

    private fun actionClearTask() {
        mSelectTaskSharedViewModel.selectTask(
            Task(
                description = "",
                name = getString(R.string.message_hint_choose_task),
                uid = -1
            )
        )

    }

    private fun onClickStartTimer(view: View) {
        val timeMinutes = getTimerMinutes()

        mServiceController?.doStartService(
            timeMinutes * 60 * 1000.toLong(),
            mLayoutStatus.chunkIndex, mTask.uid!!, getStartCommand()
        )
    }

    private fun getStartCommand(): Int {
        return if (mLayoutStatus.isTimer) return ACTION_START_TIME_SLICE else ACTION_START_BREAKTIME
    }


    private fun getTimerMinutes(): Int {
        return if (mLayoutStatus.isTimer) return mLayoutStatus.chunkSizes[mLayoutStatus.chunkIndex] else mLayoutStatus.breaktimeSizes[mLayoutStatus.breaktimeIndex]
    }

    private fun actionCancelTimer(view: View) {
        mServiceController?.doStopService()
    }

}





