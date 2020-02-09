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
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.ui.task_stats.PeriodSummaryTO
import dev.alessi.chunk.pomodoro.timer.android.util.toFormatedTime


class SelectTaskRecyclerAdapter(
    private var items: List<SelectTaskTO>,
    val onSelect: (task: SelectTaskTO) -> Unit,
    val onTaskInfoClick: (taskSummariesTO: SelectTaskTO) -> Unit,
    val onTaskArchiveClick: (taskId: Task) -> Unit,
    val onEstimateTaskClick: (task: Task) -> Unit
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
            view, taskSelectClick, taskInfoClick, archiveTaskClick, estimateTaskClick, view.context
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

    private val estimateTaskClick = View.OnClickListener {
        val taskSummariesTO = (it.tag as SelectTaskTO)
        onEstimateTaskClick(taskSummariesTO.task)
    }


    private fun remove(list: List<SelectTaskTO>, position: Int) {
        (list as MutableList).removeAt(position)
    }

    private fun removeItem(taskSummariesTO: SelectTaskTO) {
        val indexAtItems = items.indexOf(taskSummariesTO)
        remove(items, indexAtItems)

        if (isFiltering) {
            val indexAtFilterered = itemsFiltered.indexOf(taskSummariesTO)
            remove(itemsFiltered, indexAtFilterered)
            notifyItemRemoved(indexAtFilterered)
            notifyItemRangeChanged(indexAtItems, itemCount)
        } else {
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
    estimateTaskClick: View.OnClickListener,
    val context: Context
) :
    RecyclerView.ViewHolder(view) {
    private var txtTaskName: TextView = view.findViewById(R.id.txt_task_name)
    private var txtTaskDesc: TextView = view.findViewById(R.id.txt_task_description)
    private var txtTaskCreatedAt: TextView = view.findViewById(R.id.txtCreatedAt)
    //    private val chipSummary = view.findViewById<Chip>(R.id.chipSummary)
    private val btnInfo = view.findViewById<ImageButton>(R.id.btnInfo)
    private val btnArchive = view.findViewById<ImageButton>(R.id.btnArchive)

    private val btnEstimate = view.findViewById<ImageButton>(R.id.btnEstimate)


    private val txtDone = view.findViewById<TextView>(R.id.txt_done_estimate)


    private var taskSummaryTO: SelectTaskTO? = null


    init {
//        view.setOnClickListener(selectTaskClick)

        txtTaskName.setOnClickListener(selectTaskClick)
        txtTaskDesc.setOnClickListener(selectTaskClick)
        btnInfo.setOnClickListener(taskInfoClick)
        btnEstimate.setOnClickListener(estimateTaskClick)

/*
        chipSummary.setOnClickListener {
            taskSummaryTO?.rotate()
            setChipSummary(taskSummaryTO?.getPeriod())

        }
*/

        btnArchive.setOnClickListener {

            archiveTaskClick.onClick(it)
        }


    }


    fun bind(taskSummaryTO: SelectTaskTO) {

        this.taskSummaryTO = taskSummaryTO

        txtTaskDesc.text = taskSummaryTO.task.description
        txtTaskName.text = taskSummaryTO.task.name

//        chipSummary.tag = taskSummaryTO
        btnInfo.tag = taskSummaryTO
        txtTaskDesc.tag = taskSummaryTO
        txtTaskName.tag = taskSummaryTO
        btnArchive.tag = taskSummaryTO
        btnEstimate.tag = taskSummaryTO

        if (taskSummaryTO.estimationMinutes > 0) {


        }

        val summary = taskSummaryTO.getPeriod()

        setChipSummary(summary)

        val dateTime = taskSummaryTO.task.dateCreated?.time!!

        val formatDate = DateUtils.formatDateTime(
            context, dateTime, DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE or
                    DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_SHOW_WEEKDAY or DateUtils.FORMAT_NUMERIC_DATE or DateUtils.FORMAT_ABBREV_ALL
        )


//        txtTaskCreatedAt.text =
//            context.getString(R.string.label_format_created_at, formatDate)

        txtTaskCreatedAt.text = formatDate
        txtDone.text = "${taskSummaryTO.sliceMinutes.toFormatedTime()} / ${taskSummaryTO.estimationMinutes.toFormatedTime()}"
    }

    private fun setChipSummary(summary: PeriodSummaryTO?) {
        /*chipSummary.text =
            context.getString(
                R.string.label_format_summary_period_time,
                context.getString(summary?.period?.labelKey!!),
                context.resources.getQuantityString(
                    R.plurals.label_format_hours,
                    summary.minutes / 60,
                    summary.toFormatedHours()
                )
            )*/
    }


}