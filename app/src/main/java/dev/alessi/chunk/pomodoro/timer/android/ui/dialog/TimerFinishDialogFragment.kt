package dev.alessi.chunk.pomodoro.timer.android.ui.dialog

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.alessi.chunk.pomodoro.timer.android.AppUtilsProvider
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.components.customview.ClockView
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnit
import dev.alessi.chunk.pomodoro.timer.android.service.ChunkTimerService
import dev.alessi.chunk.pomodoro.timer.android.util.IntentBuilder

class TimerFinishDialogFragment : DialogFragment() {

    private var txtMessage: TextView? = null
    private var clockviewTimerFinish: ClockView? = null

    private val mTimerFinishViewModel: TimerFinishViewModel by viewModels()



    private lateinit var mAppUtils: AppUtilsProvider


    private val onSliceLoaded = Observer<WorkUnit> { wu ->

        val sizeName = mAppUtils.getSizeName(wu.sizeId)
        var taskName: String? = wu.task?.name
        taskName = if (taskName?.isEmpty()!!) null else taskName

        taskName?.let {
            updateUi(wu.timeMinutes, R.string.message_chunk_finish, sizeName, it)
        } ?: updateUi(wu.timeMinutes, R.string.message_chunk_finish_without_task, sizeName)



    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activity?.let {
            mTimerFinishViewModel.onSliceLoaded.observe(it, onSliceLoaded)
            mAppUtils = it.application as AppUtilsProvider
        }

        super.onActivityCreated(savedInstanceState)
    }


    private fun getViewLayout(@ChunkTimerService.Event event: Int): Int {
        return if (event == ChunkTimerService.Event.ON_TIME_SLICE_COMPLETED)
            R.layout.dialog_timer_finish else R.layout.dialog_breaktime_finish

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false

        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it)
            val serviceExtras = IntentBuilder.getServiceExtras(arguments!!)

            val event = serviceExtras.event
            val layout = getViewLayout(event)
            val inflatedView =
                LayoutInflater.from(context).inflate(layout, null)
            builder.setView(inflatedView)
            extractViews(inflatedView)


            if (event == ChunkTimerService.Event.ON_BREAKTIME_COMPLETED) {
                fillBreak(builder, extras = serviceExtras)
            } else {
                fillTimerFinishBuilder(builder, extras = serviceExtras)
            }


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
        txtMessage = view?.findViewById(R.id.txt_message)
        clockviewTimerFinish = view?.findViewById(R.id.clockview_timerFinish)
    }


    private fun updateUi(minutes: Int, message: Int, sizeName: String? = null, taskName: String? = null) {
        val minuteStr = context?.resources?.getQuantityString(
            R.plurals.minutes,
            minutes, minutes
        )

        clockviewTimerFinish?.minutes = minutes


        val html = if (sizeName != null) {
            clockviewTimerFinish?.clockSizeName = sizeName
            getString(message, sizeName, minuteStr, taskName)
        } else {
            getString(message, minuteStr)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtMessage?.text = Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
        } else {
            txtMessage?.text = Html.fromHtml(html)
        }


    }

    private fun fillTimerFinishBuilder(
        builder: MaterialAlertDialogBuilder,
        extras: IntentBuilder.IntentTimeExtras
    ) {
        extras.sliceId?.let {
            if (it != -1) {
                mTimerFinishViewModel.loadSlice(it)
            }

        }


    }

    private fun fillBreak(
        builder: MaterialAlertDialogBuilder,
        extras: IntentBuilder.IntentTimeExtras
    ) {
        val totalTimeMinutes =
            (extras.totalTime / 60 / 1000).toInt()

        updateUi(minutes = totalTimeMinutes, message = R.string.message_breaktime_finish)
    }


}







