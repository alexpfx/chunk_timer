package dev.alessi.chunk.pomodoro.timer.android.ui.slice_history.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnit
import dev.alessi.chunk.pomodoro.timer.android.util.MyDateUtil
import dev.alessi.chunk.pomodoro.timer.android.util.toFormatedTime
import java.util.*


class SliceHistoryAdapter(private val onFilterChangedListener: OnFilterChangedListener) : ListAdapter<DataItem, RecyclerView.ViewHolder>(
    DataItem.DiffCallback()
), HeaderViewHolder.Companion.OnClickListener {

    private val dateUtil = MyDateUtil.getInstance()
    private var mFilter: IntervalFilter = { DataItem.Interval(Date(0), Date()) }

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
    }

    fun setItems(items: List<WorkUnit>) {
        submitList(summarizeAndConvert(items))
    }

    private fun summarizeAndConvert(items: List<WorkUnit>): MutableList<DataItem>? {
        return items.filter {
            val (di, df) = mFilter.invoke()
            it.finishDate.after(di) && it.finishDate.before(df)
        }.map {
            DataItem.ItemDataItem(it.uid?.toLong()!!, it)
        }.toMutableList<DataItem>().apply {
            add(0, summarizeHeader(this))
        }
    }

    private fun summarizeHeader(items: List<DataItem>): DataItem.HeaderDataItem {
        val total: Int = items.map {
            it as DataItem.ItemDataItem
        }.toList().fold(0, { total, next: DataItem.ItemDataItem ->
            total + next.workUnit.timeMinutes
        })
        return DataItem.HeaderDataItem(id = -1, summary = total.toFormatedTime())
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder.from(
                parent, createPeriods(parent.context), this
            )
            else -> ViewHolder.from(parent)
        }
    }

    private fun createPeriods(context: Context?): List<PeriodSelectVo> {

        val resIds = arrayListOf(
            R.string.label_period_all,
            R.string.label_period_today,
            R.string.label_period_this_week,
            R.string.label_period_last_7_days,
            R.string.label_period_this_month,
            R.string.label_period_last_30_days
        )
        val now = Date()

        val filters = arrayListOf(
            { DataItem.Interval(Date(0), now) },
            { DataItem.Interval(dateUtil.startOfDay(now), now) },
            { DataItem.Interval(dateUtil.startOfWeek(now), now) },
            { DataItem.Interval(dateUtil.addDaysTo(now, -7), now) },
            { DataItem.Interval(dateUtil.startOfMonth(now), now) },
            { DataItem.Interval(dateUtil.addDaysTo(now, -30), now) }
        )

        mFilter = filters[0]

        return resIds.mapIndexed { index, it ->
            PeriodSelectVo(context?.getString(it)!!, index, filters[index])
        }.toList()
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(getItem(position) as DataItem.HeaderDataItem)
            is ViewHolder -> holder.bind(getItem(position) as DataItem.ItemDataItem)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.HeaderDataItem -> TYPE_HEADER
            else -> TYPE_ITEM
        }
    }

    override fun onClick(filter: IntervalFilter) {
        mFilter = filter
        onFilterChangedListener.onFilterChanged()
    }


}


interface ViewHolderBinder<T : DataItem> {
    fun bind(item: T)
}

sealed class DataItem {
    abstract val id: Long

    data class HeaderDataItem(override val id: Long = -1, val summary: String) : DataItem() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is HeaderDataItem) return false
            if (!super.equals(other)) return false

            if (id != other.id) return false
            if (summary != other.summary) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + id.hashCode()
            result = 31 * result + summary.hashCode()
            return result
        }
    }

    data class ItemDataItem(override val id: Long, val workUnit: WorkUnit) : DataItem()


    class DiffCallback : DiffUtil.ItemCallback<DataItem>() {

        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id

        }

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DataItem) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    data class Interval(val start: Date, val end: Date)


}

typealias IntervalFilter = () -> DataItem.Interval

interface OnFilterChangedListener {

    fun onFilterChanged()


}