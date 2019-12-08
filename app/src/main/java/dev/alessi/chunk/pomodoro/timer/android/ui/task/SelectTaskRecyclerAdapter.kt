package dev.alessi.chunk.pomodoro.timer.android.ui.task

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.ui.task_stats.PeriodSummaryTO


class SelectTaskRecyclerAdapter(
    private var items: List<SelectTaskTO>,
    val onSelect: (task: SelectTaskTO) -> Unit,
    val onTaskInfoClick: (taskSummariesTO: SelectTaskTO) -> Unit,
    val onTaskArchiveClick: (taskId: Task) -> Unit
) :
    RecyclerView.Adapter<TaskViewHolder>(), Filterable {


    private var isFiltering = false
    private var itemsFiltered: List<SelectTaskTO> = items

    private val taskInfoClick = View.OnClickListener {

        onTaskInfoClick(it.tag as SelectTaskTO)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_select_task, parent, false)

        return TaskViewHolder(
            view, taskSelectClick, taskInfoClick, archiveTaskClick, view.context
        )
    }


    private val taskSelectClick = View.OnClickListener {
        onSelect(it.tag as SelectTaskTO)
    }


    private val archiveTaskClick = View.OnClickListener {
        val taskSummariesTO = (it.tag as SelectTaskTO)
        onTaskArchiveClick(taskSummariesTO.task)
        removeItem(taskSummariesTO)
    }

    private fun remove(list: List<SelectTaskTO>, position: Int){
        (list as MutableList).removeAt(position)
    }

    private fun removeItem(taskSummariesTO: SelectTaskTO) {
        val indexAtItems = items.indexOf(taskSummariesTO)
        remove(items, indexAtItems)

        if (isFiltering){
            val indexAtFilterered = itemsFiltered.indexOf(taskSummariesTO)
            remove (itemsFiltered, indexAtFilterered)
            notifyItemRemoved(indexAtFilterered)
            notifyItemRangeChanged(indexAtItems, itemCount)
        }else{
            notifyItemRemoved(indexAtItems)
            notifyItemRangeChanged(indexAtItems, itemCount)
        }

    }


    override fun getItemCount(): Int {
        return itemsFiltered.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = itemsFiltered[position]

        holder.bind(item)
    }


    fun setItems(items: List<SelectTaskTO>) {
        this.items = items
        this.itemsFiltered = items
        notifyDataSetChanged()
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList: MutableList<SelectTaskTO>

                if (constraint.isNullOrEmpty()) {
                    filteredList = items as MutableList<SelectTaskTO>
                    isFiltering = false
                } else {
                    filteredList = mutableListOf()
                    isFiltering = true
                    for (item in items) {
                        if (item.task.description.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filteredList.add(item)
                        }
                    }
                }

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                itemsFiltered = results?.values as MutableList<SelectTaskTO>

                notifyDataSetChanged()
            }

        }
    }
}


class TaskViewHolder(
    val view: View,
    selectTaskClick: View.OnClickListener,
    taskInfoClick: View.OnClickListener,
    archiveTaskClick: View.OnClickListener,
    val context: Context
) :
    RecyclerView.ViewHolder(view) {
    private var txtTaskDesc: TextView = view.findViewById(R.id.txtTaskDesc)
    private var txtTaskCreatedAt: TextView = view.findViewById(R.id.txtTaskCreatedAt)
    private val chipSummary = view.findViewById<Chip>(R.id.chipSummary)
    private val btnInfo = view.findViewById<ImageButton>(R.id.btnInfo)
    private val btnArchive = view.findViewById<ImageButton>(R.id.btnArchive)

    private var taskSummaryTO: SelectTaskTO? = null


    init {
        view.setOnClickListener(selectTaskClick)
        btnInfo.setOnClickListener(taskInfoClick)

        chipSummary.setOnClickListener {
            taskSummaryTO?.rotate()
            setChipSummary(taskSummaryTO?.getPeriod())

        }

        btnArchive.setOnClickListener {

            archiveTaskClick.onClick(it)
        }

    }


    fun bind(taskSummaryTO: SelectTaskTO) {
        this.taskSummaryTO = taskSummaryTO

        txtTaskDesc.text = taskSummaryTO.task.description

        chipSummary.tag = taskSummaryTO
        btnInfo.tag = taskSummaryTO
        view.tag = taskSummaryTO
        btnArchive.tag = taskSummaryTO

        val summary = taskSummaryTO.getPeriod()

        setChipSummary(summary)


        val dateTime = taskSummaryTO.task.dateCreated?.time!!

        val formatDate = DateUtils.formatDateTime(
            context, dateTime, DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE or
                    DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_SHOW_WEEKDAY or DateUtils.FORMAT_ABBREV_ALL
        )

        txtTaskCreatedAt.text =
            context.getString(R.string.label_format_created_at, formatDate)
    }

    private fun setChipSummary(summary: PeriodSummaryTO?) {
        chipSummary.text =
            context.getString(
                R.string.label_format_summary_period_time,
                context.getString(summary?.period?.labelKey!!),
                context.resources.getQuantityString(
                    R.plurals.label_format_hours,
                    summary.minutes / 60,
                    summary.toFormatedHours()
                )
            )
    }


}