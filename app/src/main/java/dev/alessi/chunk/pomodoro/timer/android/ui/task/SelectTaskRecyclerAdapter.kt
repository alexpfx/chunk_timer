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


class SelectTaskRecyclerAdapter (
    private val items: MutableList<Task>,
    val onSelect: (task: Task) -> Unit,
    val onTaskInfoClick: (task: Task) -> Unit
) :
    RecyclerView.Adapter<TaskViewHolder>(), Filterable {

    private var itemsFiltered: MutableList<Task> = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_select_task, parent, false)

        return TaskViewHolder(view, ::taskSelect, ::taskInfoClick, context = parent.context)
    }

    private fun taskSelect(v: View) {
        onSelect(v.tag as Task)
    }

    private fun taskInfoClick (v: View){
        onTaskInfoClick(v.tag as Task)
    }


    override fun getItemCount(): Int {
        return itemsFiltered.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = itemsFiltered[position]

        holder.bind(item)

    }


    fun updateItems(tasks: List<Task>) {
        this.items.clear()
        for (task in tasks) {
            if (task.uid != -1) {
                this.items.add(task)
            }
        }

        itemsFiltered = items

        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList: MutableList<Task>

                if (constraint.isNullOrEmpty()){
                    filteredList = items
                }else{
                    filteredList = mutableListOf()
                    for (item in items) {
                        if (item.description.toLowerCase().contains(constraint.toString().toLowerCase())){
                            filteredList.add(item)
                        }
                    }
                }

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                itemsFiltered = results?.values as MutableList<Task>

                notifyDataSetChanged()
            }

        }
    }
}


class TaskViewHolder(
    val view: View,
    val onSelect: (v: View) -> Unit,
    val taskInfoClick: (v: View) -> Unit,
    val context: Context
) :
    RecyclerView.ViewHolder(view) {
    private var txtTaskDesc: TextView = view.findViewById(R.id.txtTaskDesc)
    private var txtTaskCreatedAt: TextView = view.findViewById(R.id.txtTaskCreatedAt)
    private var btnTaskInfo = view.findViewById<ImageButton>(R.id.btnTaskInfo)

    init {
        view.setOnClickListener {
            onSelect(it)
        }

        btnTaskInfo.setOnClickListener{
            taskInfoClick(it)
        }

    }

    fun bind(task: Task) {
        txtTaskDesc.text = task.description
        btnTaskInfo.tag = task
        view.tag = task


        val formatDate = DateUtils.formatDateTime(
            context,
            task.dateCreated?.time!!, DateUtils.FORMAT_ABBREV_ALL
        )

        val formatTime = DateUtils.formatDateTime(context, task.dateCreated?.time!!, DateUtils.FORMAT_SHOW_TIME)


        txtTaskCreatedAt.text = "Criado em: $formatDate às $formatTime"

    }


}