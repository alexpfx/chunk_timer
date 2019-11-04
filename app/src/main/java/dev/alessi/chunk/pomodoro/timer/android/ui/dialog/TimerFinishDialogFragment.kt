package dev.alessi.chunk.pomodoro.timer.android.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.platform.ChunkTimerService

class TimerFinishDialogFragment : DialogFragment() {

    var txtTimer: TextView? = null
    var txtSize: TextView? = null
    var txtTask: TextView? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false

        val dialog = activity?.let {
            val builder = MaterialAlertDialogBuilder(it)


            @ChunkTimerService.TimerState val type =
                arguments?.getInt(ChunkTimerService.extra_param_status)




            if (ChunkTimerService.TimerState.status_running_break == type) {
                val inflatedView =
                    LayoutInflater.from(context).inflate(R.layout.dialog_breaktime_finish, null)
                builder.setView(inflatedView)

                extractViews(inflatedView)

                fillBreak(builder, arguments!!)

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

        val size = arg.getInt(ChunkTimerService.extra_param_sizeIndex, -1)
        val taskName = arg.getString(ChunkTimerService.extra_param_taskname)


        val totalTimeMinutes =
            arg.getLong(ChunkTimerService.extra_param_total_time_millis) / 60 / 1000

        txtTimer?.text = context?.resources?.getQuantityString(
            R.plurals.minutes,
            totalTimeMinutes.toInt(),
            totalTimeMinutes
        )
        txtTask?.text = taskName
        txtSize?.text = size.toString()

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







