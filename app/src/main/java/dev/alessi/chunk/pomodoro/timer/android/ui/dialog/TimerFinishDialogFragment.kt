package dev.alessi.chunk.pomodoro.timer.android.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.alessi.chunk.pomodoro.timer.android.AppUtilsProvider
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnit
import dev.alessi.chunk.pomodoro.timer.android.service.ChunkTimerService
import dev.alessi.chunk.pomodoro.timer.android.util.IntentBuilder

class TimerFinishDialogFragment : DialogFragment() {

    private var txtTimer: TextView? = null
    private var txtSize: TextView? = null
    private var txtTask: TextView? = null
    private val mTimerFinishViewModel: TimerFinishViewModel by viewModels()

    private var mSlice: WorkUnit? = null
    private lateinit var mAppUtils: AppUtilsProvider


    private val onSliceLoaded = Observer<WorkUnit> {
        mSlice = it


        updateUi()
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
        txtTimer = view?.findViewById(R.id.txtTimer)
        txtSize = view?.findViewById(R.id.txtSize)
        txtTask = view?.findViewById(R.id.card_task)
    }


    private fun updateUi() {

        txtTimer?.text = context?.resources?.getQuantityString(
            R.plurals.minutes,
            mSlice?.timeMinutes ?: 0, mSlice?.timeMinutes ?: 0

        )

        txtSize?.text = mAppUtils.getSizeName(mSlice?.sizeId ?: -1)
        txtTask?.text = mSlice?.task?.description ?: ""

    }

    private fun fillTimerFinishBuilder(
        builder: MaterialAlertDialogBuilder,
        extras: IntentBuilder.IntentTimeExtras
    ) {
        builder.setTitle(R.string.message_title_timer_finish)
        builder.setMessage(R.string.message_content_timer_finish)

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

        builder.setTitle(R.string.message_title_timer_break_finish)
        builder.setMessage(R.string.message_content_timer_break_finish)
        val totalTimeMinutes =
            extras.totalTime / 60 / 1000

        txtTimer?.text = context?.resources?.getQuantityString(
            R.plurals.minutes,
            totalTimeMinutes.toInt(),
            totalTimeMinutes
        )

//        updateUi()
    }


}







