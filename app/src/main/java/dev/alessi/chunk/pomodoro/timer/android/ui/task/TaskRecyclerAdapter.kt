package dev.alessi.chunk.pomodoro.timer.android.ui.task

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.database.Task
import dev.alessi.chunk.pomodoro.timer.android.database.TaskSummary

class TaskRecyclerAdapter(
    val items: MutableList<TaskSummary>,
    val onSelect: (task: Task) -> Unit,
    val onDescriptionClick: (task: Task) -> Unit
) :
    RecyclerView.Adapter<TaskViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)

        return TaskViewHolder(view, ::btnSelect, ::taskSelect)
    }

    private fun btnSelect(v: View) {
        onSelect(v.tag as Task)
    }

    private fun taskSelect(v: View) {
        onDescriptionClick(v.tag as Task)


    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = items[position]

        holder.bind(item)

    }

    fun updateItems(tasks: List<Task>) {
        this.items.clear()
        for (task in tasks) {
            this.items.add(TaskSummary(task))
        }

        notifyDataSetChanged()
    }
}


class TaskViewHolder(
    val view: View,
    val callback: (v: View) -> Unit,
    val onDescClick: (view: View) -> Unit
) :
    RecyclerView.ViewHolder(view) {
    private var txtTaskDesc: TextView = view.findViewById(R.id.txtTaskDesc)
    private var txtSizeSummary: TextView = view.findViewById(R.id.txtSizeSummary)
    private var txtTimeSummary: TextView = view.findViewById(R.id.txtTimeSummary)
    private val btnSelectTask: ImageButton = view.findViewById(R.id.btnSelectTask)

    init {
        btnSelectTask.setOnClickListener {
            callback(it)
        }

        txtTaskDesc.setOnClickListener {
            onDescClick(it)
        }
    }

    fun bind(taskSummary: TaskSummary) {
        txtTaskDesc.text = taskSummary.task.description
        txtSizeSummary.text = taskSummary.sizeSummary
        txtTimeSummary.text = taskSummary.timeSummary
        txtTaskDesc.tag = taskSummary.task
        btnSelectTask.tag = taskSummary.task

    }


}