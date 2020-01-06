package dev.alessi.chunk.pomodoro.timer.android.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnit
import dev.alessi.chunk.pomodoro.timer.android.service.ChunkTimerService

class TimerFinishDialogFragment : DialogFragment() {

    private var txtTimer: TextView? = null
    private var txtSize: TextView? = null
    private var txtTask: TextView? = null
    private lateinit var mTimerFinishViewModel: TimerFinishViewModel


    private var mSlice: WorkUnit? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        mTimerFinishViewModel = ViewModelProviders.of(this).get(TimerFinishViewModel::class.java)
        super.onCreate(savedInstanceState)
    }


    private val onSliceLoaded = Observer<WorkUnit> {
        mSlice = it

        updateUi()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        if (activity != null){
            mTimerFinishViewModel = ViewModelProviders.of(activity!!)[TimerFinishViewModel::class.java]
            mTimerFinishViewModel.onSliceLoaded.observe(activity!!, onSliceLoaded)
        }

        super.onActivityCreated(savedInstanceState)
    }


    fun getViewLayout(@ChunkTimerService.TimerState state: Int): Int {
        return if (state == ChunkTimerService.TimerState.status_running_timer)
            R.layout.dialog_timer_finish else R.layout.dialog_breaktime_finish

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false

        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it)

            @ChunkTimerService.TimerState val type =
                arguments?.getInt(ChunkTimerService.extra_param_status)!!


            val layout = getViewLayout(type)
            val inflatedView =
                LayoutInflater.from(context).inflate(layout, null)
            extractViews(inflatedView)

            fillBreak(builder, arguments!!)
            fillTimerFinishBuilder(builder, arguments!!)

            builder.setPositiveButton(
                android.R.string.ok
            ) { _, _ ->
                dismiss()
            }


            builder.create()
            builder.show()

        } ?: throw IllegalStateException("activity couldn't be null")


    }

    private fun extractViews(view: View?) {
        txtTimer = view?.findViewById(R.id.txtTimer)
        txtSize = view?.findViewById(R.id.txtSize)
        txtTask = view?.findViewById(R.id.card_task)
    }


    private fun updateUi() {

        txtTimer?.text = context?.resources?.getQuantityString(
            R.plurals.minutes,
            mSlice?.timeMinutes ?: 0, mSlice?.timeMinutes ?: 0

        )

        //TODO mostrar icone do size
        txtSize?.text = mSlice?.taskSize?.name ?: ""
        txtTask?.text = mSlice?.task?.description ?: ""

    }

    private fun fillTimerFinishBuilder(
        builder: MaterialAlertDialogBuilder,
        arg: Bundle
    ) {
        builder.setTitle(R.string.message_title_timer_finish)
        builder.setMessage(R.string.message_content_timer_finish)

        updateUi()

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







