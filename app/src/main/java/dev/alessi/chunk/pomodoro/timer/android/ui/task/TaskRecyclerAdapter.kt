package dev.alessi.chunk.pomodoro.timer.android.ui.task

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.database.Task

class TaskRecyclerAdapter(val items: List<Task>): RecyclerView.Adapter<TaskViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = items[position]
        holder.txtTaskDesc.text = item.description
        holder.txtTaskStats.text = "2 GG 4 G"
        holder.txtTaskTotalTime.text = "25"


    }
}



class TaskViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    var txtTaskDesc: TextView = view.findViewById(R.id.txtTaskDesc)
    var txtTaskStats: TextView = view.findViewById(R.id.txtTaskStats)
    var txtTaskTotalTime: TextView = view.findViewById(R.id.txtTaskTotalTime)
}