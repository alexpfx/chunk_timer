package dev.alessi.chunk.pomodoro.timer.android.ui.slice_history.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.alessi.chunk.pomodoro.timer.android.AppUtilsProvider
import dev.alessi.chunk.pomodoro.timer.android.ClockView
import dev.alessi.chunk.pomodoro.timer.android.R
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

class ViewHolder(view: View) : RecyclerView.ViewHolder(view),
    ViewHolderBinder<DataItem.ItemDataItem> {

    private val txtWhen = view.findViewById<TextView>(R.id.txt_when)
    private val txtDescription = view.findViewById<TextView>(R.id.txt_description)
    private val clockView = view.findViewById<ClockView>(R.id.clockView)
    val context = view.context!!
    private var mAppUtils: AppUtilsProvider

    init {
         mAppUtils = (context.applicationContext as AppUtilsProvider)
    }


    companion object {
        fun from(viewGroup: ViewGroup): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(
                    viewGroup.context
                ).inflate(R.layout.item_slice_history, viewGroup, false)
            )
        }
    }

    override fun bind(item: DataItem.ItemDataItem) {
        val workUnit = item.workUnit

        clockView?.minutes = workUnit.timeMinutes

        val prettyTime = PrettyTime(Date())
        txtWhen?.text = prettyTime.format(workUnit.finishDate)

        txtDescription?.text = context.getString(
            R.string.message_content_slice_of_size_time,
            mAppUtils.getSizeName(workUnit.sizeId),
            context.resources.getQuantityString(R.plurals.minutes, workUnit.timeMinutes, workUnit.timeMinutes)
        )

    }

}