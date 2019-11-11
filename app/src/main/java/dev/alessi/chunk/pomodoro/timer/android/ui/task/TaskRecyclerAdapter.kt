package dev.alessi.chunk.pomodoro.timer.android.ui.task

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.database.Task

class TaskRecyclerAdapter(val items: MutableList<Task>, val onSelect: (task: Task) -> Unit, val onDescriptionClick: (task: Task) -> Unit) :
    RecyclerView.Adapter<TaskViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)

        return TaskViewHolder(view, ::btnSelect, ::taskSelect)
    }

    private fun btnSelect(v: View) {
        onSelect(v.tag as Task)
    }

    private fun taskSelect(v: View){
        onDescriptionClick(v.tag as Task)


    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = items[position]
        holder.txtTaskDesc.text = item.description
        holder.txtTaskDesc.tag = item
        holder.view.tag = item
        holder.txtTaskStats.text = "2 GG 4 G"
        holder.btnSelectTask.tag = item

//        holder.txtTaskTotalTime.text = "25"


    }

    fun updateItems(tasks: List<Task>) {
        this.items.clear()
        items.addAll(tasks)
        notifyDataSetChanged()
    }
}


class TaskViewHolder(val view: View, val callback: (v: View) -> Unit, val onDescClick: (view: View) -> Unit) :
    RecyclerView.ViewHolder(view) {
    var txtTaskDesc: TextView = view.findViewById(R.id.txtTaskDesc)
    var txtTaskStats: TextView = view.findViewById(R.id.txtTaskStats)
    val btnSelectTask: ImageButton = view.findViewById(R.id.btnSelectTask)

    init {
        btnSelectTask.setOnClickListener {
            callback(it)
        }

        txtTaskDesc.setOnClickListener{
            onDescClick(it)
        }
    }


}