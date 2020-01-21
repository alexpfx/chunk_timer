package dev.alessi.chunk.pomodoro.timer.android.ui.task_stats

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.alessi.chunk.pomodoro.timer.android.ClockView
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnit
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

class TaskSliceHistoryAdapter(private val itemList: List<WorkUnit>) :
    RecyclerView.Adapter<BaseVW<*>>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVW<*> {
        return if (viewType == 0) {
            val periods = createPeriods(parent.context)


            HeaderViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_slice_history_header, parent, false),
                periods, ::onSelect
            )
        } else {
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_slice_history, parent, false))
        }
    }

    override fun getItemCount() = itemList.size + 1

    override fun onBindViewHolder(holder: BaseVW<*>, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(itemList[position - 1])
        } else if (holder is HeaderViewHolder) {
            holder.bind(summarize(itemList.filter(mFilter)))
        }
    }

    private var mFilter: (WorkUnit) -> Boolean = { true }

    private fun onSelect(item: PeriodSummaryTO.TimePeriod, update: Boolean) {
        mFilter = item.filter

        if (update) notifyDataSetChanged()
    }


    private fun createPeriods(context: Context): Array<PeriodSummaryTO.TimePeriod> {
        val values = PeriodSummaryTO.Period.values()

        return Array(values.size) {
            val period = values[it]
            PeriodSummaryTO.TimePeriod(context.getString(period.labelKey)) { workUnit ->
                val (di, df) = period.dateInterval()
                di.after(workUnit.finishDate) && df.before(workUnit.finishDate)
            }
        }

    }

    private fun summarize(itemList: List<WorkUnit>): PeriodSummaryTO {
        val p = PeriodSummaryTO.from(PeriodSummaryTO.Period.ALL, itemList)
        return p
    }


    override fun getItemViewType(position: Int): Int {
        return if (position == 0) 0 else 1
    }

}


class ViewHolder(view: View) : BaseVW<WorkUnit>(view) {
    private var txtDesc: TextView? = view.findViewById(R.id.txt_description)
    private var txtWhen: TextView? = view.findViewById(R.id.txt_when)
    private var clockView: ClockView? = view.findViewById(R.id.clockView)


    //    private var viewDivider = view.findViewById<View>(R.id.view_internal_divider2)
    private var context = itemView.context


    override fun bind(workUnit: WorkUnit) {
        clockView?.minutes = workUnit.timeMinutes


        val prettyTime = PrettyTime(Date())
        txtWhen?.text = prettyTime.format(workUnit.finishDate)

        txtDesc?.text = context.getString(
            R.string.message_content_slice_of_size_time,
            "médio",
            context.resources.getQuantityString(R.plurals.minutes, workUnit.timeMinutes, workUnit.timeMinutes)
        )


    }

}

class HeaderViewHolder(
    val view: View,
    val periods: Array<PeriodSummaryTO.TimePeriod>,
    val onSelect: (item: PeriodSummaryTO.TimePeriod, update: Boolean) -> Unit

) : BaseVW<PeriodSummaryTO>(view),
    AdapterView.OnItemClickListener {
    private val txtPeriodName: TextView? = view.findViewById(R.id.txt_period_name)
    private val txtTimeSummary: TextView? = view.findViewById(R.id.txt_time_summary)
    private val spinnerPeriod: AutoCompleteTextView? = view.findViewById(R.id.dropdown_menu)

    init {

        val defaultPeriod = periods[0]
        spinnerPeriod?.setText(defaultPeriod.name)
        onSelect(defaultPeriod, false)

        ArrayAdapter<PeriodSummaryTO.TimePeriod>(view.context, R.layout.item_dropdown, periods).also {
            spinnerPeriod?.setAdapter(it)
        }

        spinnerPeriod?.onItemClickListener = this
    }


    override fun bind(data: PeriodSummaryTO) {
        txtTimeSummary?.text = data.toFormatedTime()

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        txtPeriodName?.text = periods[position].toString()
        onSelect(periods[position], true)
    }

}


abstract class BaseVW<T>(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: T)
}


