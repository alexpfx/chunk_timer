//package dev.alessi.chunk.pomodoro.timer.android.ui.task_stats
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.AdapterView
//import android.widget.ArrayAdapter
//import android.widget.AutoCompleteTextView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import dev.alessi.chunk.pomodoro.timer.android.components.customview.ClockView
//import dev.alessi.chunk.pomodoro.timer.android.R
//import dev.alessi.chunk.pomodoro.timer.android.database.WorkUnit
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import org.ocpsoft.prettytime.PrettyTime
//import java.util.*
//
//class TaskSliceHistoryAdapter(
//    private var itemList: MutableList<DataItem>,
//    val context: Context
//) :
//    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//    private val ITEM_VIEW_TYPE_HEADER = 0
//    private val ITEM_VIEW_TYPE_ITEM = 1
//    private val adapterScope = CoroutineScope(Dispatchers.Default)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return when (viewType) {
//            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(parent)
//            ITEM_VIEW_TYPE_ITEM -> ItemViewHolder.from(parent)
//            else -> throw IllegalStateException("cannot be")
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return itemList.size
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        when (holder) {
//
//        }
//    }
//
//    private var mFilter: (WorkUnit) -> Boolean = { true }
//
//    private fun onSelect(item: PeriodSummaryTO.TimePeriod, update: Boolean) {
//        mFilter = item.filter
//        itemList = itemList.filter(mFilter).toMutableList()
//        println("itemList $itemList")
//
//        if (update) notifyDataSetChanged()
//    }
//
//    fun setItems(list: List<WorkUnit>) {
//        adapterScope.launch {
//            val items = listOf(getHeader()) + list.map {
//                DataItem.BodyDataItem(it)
//            }
//            withContext(Dispatchers.Main){
//
//            }
//
//        }
//
//    }
//
//    private fun getHeader(): DataItem.HeaderDataItem {
//        return DataItem.HeaderDataItem(createPeriods(context))
//
//    }
//
//    private fun createPeriods(context: Context): List<PeriodSummaryTO.TimePeriod> {
//        return PeriodSummaryTO.Period.values().map { period: PeriodSummaryTO.Period ->
//            PeriodSummaryTO.TimePeriod(context.getString(period.labelKey)) { workUnit ->
//                val (di, df) = period.dateInterval()
//
//                di.after(workUnit.finishDate) && df.before(workUnit.finishDate)
//
//            }
//        }
//
//    }
//
//    private fun summarize(itemList: List<WorkUnit>): PeriodSummaryTO {
//        val p = PeriodSummaryTO.from(itemList.filter(mFilter))
//        return p
//    }
//
//
//    override fun getItemViewType(position: Int): Int {
//        return when (itemList[position]) {
//            is DataItem.HeaderDataItem -> ITEM_VIEW_TYPE_HEADER
//            is DataItem.BodyDataItem -> ITEM_VIEW_TYPE_ITEM
//        }
//    }
//
//}
//
//
//class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//    private var txtDesc: TextView? = view.findViewById(R.id.txt_description)
//    private var txtWhen: TextView? = view.findViewById(R.id.txt_when)
//    private var clockView: ClockView? = view.findViewById(R.id.clockView)
//
//    //    private var viewDivider = view.findViewById<View>(R.id.view_internal_divider2)
//    private var context = itemView.context
//
//    companion object {
//        fun from(parent: ViewGroup): ItemViewHolder {
//            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_slice_history, parent, false)
//            return ItemViewHolder(view)
//        }
//    }
//
//
//    fun bind(workUnit: WorkUnit) {
//        clockView?.minutes = workUnit.timeMinutes
//
//        val prettyTime = PrettyTime(Date())
//        txtWhen?.text = prettyTime.format(workUnit.finishDate)
//
//        txtDesc?.text = context.getString(
//            R.string.message_content_slice_of_size_time,
//            "médio",
//            context.resources.getQuantityString(R.plurals.minutes, workUnit.timeMinutes, workUnit.timeMinutes)
//        )
//
//    }
//
//}
//
//class HeaderViewHolder(
//    val view: View,
//    val periods: Array<PeriodSummaryTO.TimePeriod>,
//    val onSelect: (item: PeriodSummaryTO.TimePeriod, update: Boolean) -> Unit
//
//) : RecyclerView.ViewHolder(view),
//    AdapterView.OnItemClickListener {
//
//    private val txtPeriodName: TextView? = view.findViewById(R.id.txt_period_name)
//    private val txtTimeSummary: TextView? = view.findViewById(R.id.txt_time_summary)
//    private val spinnerPeriod: AutoCompleteTextView? = view.findViewById(R.id.dropdown_menu)
//
//    init {
//
//        val defaultPeriod = periods[0]
//        spinnerPeriod?.setText(defaultPeriod.name)
//        onSelect(defaultPeriod, false)
//
//        ArrayAdapter<PeriodSummaryTO.TimePeriod>(view.context, R.layout.item_dropdown, periods).also {
//            spinnerPeriod?.setAdapter(it)
//        }
//
//        spinnerPeriod?.onItemClickListener = this
//    }
//
//    companion object {
//        fun from(
//            parent: ViewGroup,
//            periods: Array<PeriodSummaryTO.TimePeriod>,
//            onSelect: (item: PeriodSummaryTO.TimePeriod, update: Boolean) -> Unit
//        ): HeaderViewHolder {
//            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_slice_history_header, parent, false)
//            return HeaderViewHolder(v, periods, onSelect)
//        }
//    }
//
//
//    fun bind(periodSummaryTO: PeriodSummaryTO) {
//        txtTimeSummary?.text = periodSummaryTO.toFormatedTime()
//
//    }
//
//    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        txtPeriodName?.text = periods[position].toString()
//        onSelect(periods[position], true)
//    }
//}
//
//sealed class DataItem {
//    abstract val id: Long
//
//    data class BodyDataItem(val workUnit: WorkUnit) : DataItem() {
//        override val id: Long = workUnit.uid?.toLong() ?: -1
//    }
//
//    data class HeaderDataItem(val timePeriod: List<PeriodSummaryTO.TimePeriod>) : DataItem() {
//        override val id: Long = Long.MIN_VALUE
//    }
//
//
//}
//
//
//
//
//
//
//
//
