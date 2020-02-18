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
import dev.alessi.chunk.pomodoro.timer.android.databinding.FragmentTimerBinding
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

class TimerFragment : Fragment() {

    private val mConfigTimersSharedViewModel: ConfigTimersSharedViewModel by viewModels(::requireActivity)
    private val mSelectTaskSharedViewModel: SelectTaskSharedViewModel by viewModels(::requireActivity)
    private val mMainActivityControlSharedViewModel: MainActivityControlViewModel by viewModels(::requireActivity)
    private val mLayoutEventViewModel: TimerLayoutEventViewModel by viewModels()
    private lateinit var mAppUtils: AppUtilsProvider
    private var mLayoutStatus = TimerLayoutEventViewModel.TimerFragmentViewState()
    private var mGson: Gson = Gson()
    private var mTask: Task = Task(name = "", description = "", uid = -1)
    private var mServiceController: ChunkTimerServiceControl? = null
    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
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
    private val mServiceEventBroadcastReciver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            handleServiceEvent(intent)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true
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

        binding.clockviewgroupChunkTimers.apply {
            updateSizes(mLayoutStatus.chunkSizes)
            syncSelection(mLayoutStatus.breaktimeIndex)
        }
        binding.clockviewgroupBreaktimes.apply {
            updateSizes(mLayoutStatus.breaktimeSizes)
            syncSelection(mLayoutStatus.chunkIndex)
        }

        val timeMinutes = getTimerMinutes()

        val formatedTime = DateUtils.formatElapsedTime(timeMinutes * 60.toLong())
//        txtTimerDisplay.text = formatedTime

        val timeMillis = timeMinutes * 60 * 1000.toLong()

        updateClockTimer(timeMillis, timeMillis, -1)
//        mMainActivityControlSharedViewModel.updateSubtitle("")


    }

    private fun updateTaskRelatedViews() {
        setViewVisibility(binding.groupTask, mLayoutStatus.showGroupTask)
        setViewVisibility(binding.txtTaskEmpty, mLayoutStatus.showTxtTaskEmpty)
    }

    private fun updateTimerTypeRelatedViews() {
        setViewVisibility(binding.clockviewgroupChunkTimers, mLayoutStatus.isTimer, binding.clockviewgroupBreaktimes)
        binding.txtTimerTitle.setText(mLayoutStatus.txtTimerTitleRes)
    }

    private fun updateTimerRelatedViews() {
        setViewVisibility(binding.btnStartTimer, mLayoutStatus.showStartTimerButton, binding.btnCancelTimer)
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

        mLayoutEventViewModel.fireEvent(
            CLOCK_TIMER_SETUP,
            mLayoutStatus.copy(
                chunkSizes = chunktimesList,
                breaktimeSizes = breaktimes,
                chunkIndex = chunktimeIndex,
                breaktimeIndex = breaktimeIndex
            )
        )


    }

    private fun getSharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    private fun updateTask() {
        binding.txtTaskName.text = mTask.name
        binding.txtTaskDesc.text = mTask.description
    }

    private fun updateClockTimer(timeLeft: Long, totalTime: Long, @ChunkCountDownTimer.Type type: Int) {
//        val formatedTime = DateUtils.formatElapsedTime(timeLeft / 1000)
        val formatedTime = timeLeft.toFormatedElapsedInMinutes()

        binding.txtMainTimer.text = formatedTime

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
        _binding = FragmentTimerBinding.inflate(inflater, container, false)

        return _binding?.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initEventListeners()





        super.onViewCreated(view, savedInstanceState)
    }

    private fun initEventListeners() {
        binding.btnStartTimer.setOnClickListener(::onClickStartTimer)
        binding.toggleGroupTimerBreak.addOnButtonCheckedListener(onTimerModeCheckedListener)

        binding.clockviewgroupChunkTimers.apply {
            this.onClockViewSelected = this@TimerFragment.onClockViewSelected
            this.onViewMoreButtonClick = this@TimerFragment.onViewMoreButtonClick
        }

        binding.clockviewgroupBreaktimes.apply {
            this.onClockViewSelected = this@TimerFragment.onClockViewBreakSelected
            this.onViewMoreButtonClick = this@TimerFragment.onViewMoreBreakButtonClick
        }

        binding.btnCancelTimer.setOnClickListener(::actionCancelTimer)
        binding.btnClearTask.setOnClickListener(::onClickClearTask)

        val txtTaskName = binding.txtTaskName
        txtTaskName.movementMethod = ScrollingMovementMethod()

        arraySetOf<TextView>(binding.txtTaskEmpty, binding.txtTaskDesc, txtTaskName).forEach {
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
        binding.txtTaskName.isEnabled = enabled
        binding.txtTaskDesc.isEnabled = enabled
        binding.txtTaskEmpty.isEnabled = enabled
        binding.clockviewgroupChunkTimers.isEnabled = enabled
        binding.clockviewgroupBreaktimes.isEnabled = enabled

        binding.toggleButtonTimer.isEnabled = enabled
        binding.toggleButtonBreak.isEnabled = enabled

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

    companion object {
        const val DEFAULT_TIME_BREAK = 10
        const val DEFAULT_JSON_SIZES = "[18,24,36,48,60]"
        const val DEFAULT_JSON_BREAKS = "[5,10,20]"
        const val KEY_SIZE_JSON_ARRAY = "KEY_SIZE_JSON_ARRAY"
        const val KEY_BREAKTIME_JSON_ARRAY = "KEY_BREAKTIME_JSON_ARRAY"
        const val KEY_LAST_INDEX = "key_last_index"
        const val KEY_LAST_BREAKTIME_INDEX = "KEY_LAST_BREAKTIME_INDEX"
    }

} //524





