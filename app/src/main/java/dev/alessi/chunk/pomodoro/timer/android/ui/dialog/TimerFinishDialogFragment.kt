package dev.alessi.chunk.pomodoro.timer.android.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.RepositoryProvider
import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnit
import dev.alessi.chunk.pomodoro.timer.android.platform.ChunkTimerService
import dev.alessi.chunk.pomodoro.timer.android.platform.SoundEffectManager
import dev.alessi.chunk.pomodoro.timer.android.repository.TaskRepository
import dev.alessi.chunk.pomodoro.timer.android.settings.SettingsFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class TimerFinishDialogFragment : DialogFragment() {

    var txtTimer: TextView? = null
    var txtSize: TextView? = null
    var txtTask: TextView? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    var mTask: Task? = null
    var mSize: Int = 0
    var mTotalTime : Int = -1



    private var mTimerRingIndex = -1
    private var mBreaktimeRingIndex = -1
    private lateinit var sfm: SoundEffectManager
    lateinit var mTaskRepository: TaskRepository

    private fun loadSoundEffectPrefs() {
        val pm = PreferenceManager.getDefaultSharedPreferences(this.activity!!)
        mBreaktimeRingIndex = pm.getInt(SettingsFragment.pref_ring_breaktime, -1)
        mTimerRingIndex = pm.getInt(SettingsFragment.pref_ring_timer, -1)

    }

    override fun onAttach(context: Context) {
        mTaskRepository = (activity?.applicationContext as RepositoryProvider).getTaskRepository()
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sfm = SoundEffectManager(this.activity!!)

    }

    override fun onDestroy() {
        sfm.dispose()
        super.onDestroy()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false

        val dialog = activity?.let {
            val builder = MaterialAlertDialogBuilder(it)

            @ChunkTimerService.TimerState val type =
                arguments?.getInt(ChunkTimerService.extra_param_status)


            loadSoundEffectPrefs()


            if (ChunkTimerService.TimerState.status_running_break == type) {
                val inflatedView =
                    LayoutInflater.from(context).inflate(R.layout.dialog_breaktime_finish, null)
                builder.setView(inflatedView)

                extractViews(inflatedView)

                fillBreak(builder, arguments!!)
                sfm.play(mBreaktimeRingIndex)

            } else {
                val inflatedView = LayoutInflater.from(context).inflate(
                    R.layout.dialog_timer_finish,
                    null
                )
                builder.setView(
                    inflatedView
                )

                extractViews(inflatedView)

                fillTimerFinishBuilder(builder, arguments!!)
                sfm.play(mTimerRingIndex)
            }


            builder.setPositiveButton(
                android.R.string.ok
            ) { _, _ ->
                dismiss()
            }


            builder.create()
            builder.show()
        } ?: throw IllegalStateException("activity couldn't be null")

        return dialog

    }

    private fun extractViews(view: View?) {
        txtTimer = view?.findViewById(R.id.txtTimer)
        txtSize = view?.findViewById(R.id.txtSize)
        txtTask = view?.findViewById(R.id.txtTask)
    }




    private fun fillTimerFinishBuilder(
        builder: MaterialAlertDialogBuilder,
        arg: Bundle
    ) {
        builder.setTitle(R.string.message_title_timer_finish)
        builder.setMessage(R.string.message_content_timer_finish)

        mSize = arg.getInt(ChunkTimerService.extra_param_sizeIndex, -1)
        val taskId = arg.getInt(ChunkTimerService.extra_param_task_id, -1)
        mTotalTime =
            (arg.getLong(ChunkTimerService.extra_param_total_time_millis) / 60 / 1000).toInt()

        loadTaskAndUpdateUi(taskId)

    }

    private fun loadTaskAndUpdateUi(taskId: Int) {
        if (taskId != -1){
            scope.launch {
                mTask = withContext(Dispatchers.IO){
                    mTaskRepository.loadTask(taskId)
                }
                val wUnit = WorkUnit(finishDate = Date(), size = mSize, taskId = taskId, timeMinutes = mTotalTime)

                updateUi()
            }
        }
    }

    private fun updateUi() {
        txtTimer?.text = context?.resources?.getQuantityString(
            R.plurals.minutes,
            mTotalTime.toInt(),
            mTotalTime
        )
        txtTask?.text = mTask?.description?: ""
        txtSize?.text = mSize.toString()
    }

    private fun fillBreak(
        builder: MaterialAlertDialogBuilder,
        arg: Bundle
    ) {

        builder.setTitle(R.string.message_title_timer_break_finish)
        builder.setMessage(R.string.message_content_timer_break_finish)
        val totalTimeMinutes =
            arg.getLong(ChunkTimerService.extra_param_total_time_millis) / 60 / 1000

        txtTimer?.text = context?.resources?.getQuantityString(
            R.plurals.minutes,
            totalTimeMinutes.toInt(),
            totalTimeMinutes
        )
    }


}







