package dev.alessi.chunk.pomodoro.timer.android.ui.task_stats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.components.StatsSizeItem
import kotlin.random.Random

class TaskStatsRecyclerAdapter(private val itemList: ArrayList<PeriodSummaryTO>) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_task_stats, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val random = Random(System.currentTimeMillis())

    private var layoutSlicesSummary: LinearLayout = view.findViewById(R.id.layoutSlicesSummary)

    private var txtTimeSummary = view.findViewById<TextView>(R.id.txtTimeSummary)
    private var txtPeriod = view.findViewById<TextView>(R.id.txtPeriod)

    fun bind(periodSummaryTO: PeriodSummaryTO) {
        txtTimeSummary.text = periodSummaryTO.timeSummary
        txtPeriod.text = periodSummaryTO.periodName

        layoutSlicesSummary.addView(createItem(random.nextInt(15, 124)))
        layoutSlicesSummary.addView(createItem(random.nextInt(1, 124)))
        layoutSlicesSummary.addView(createItem(random.nextInt(50, 150)))
        layoutSlicesSummary.addView(createItem(random.nextInt(15, 1100)))
        layoutSlicesSummary.addView(createItem(random.nextInt(15, 124)))
        layoutSlicesSummary.addView(createItem(random.nextInt(1, 124)))
        layoutSlicesSummary.addView(createItem(random.nextInt(50, 150)))
        layoutSlicesSummary.addView(createItem(random.nextInt(15, 1100)))
        layoutSlicesSummary.addView(createItem(random.nextInt(50, 450)))

    }

    fun createItem(text: Int): StatsSizeItem{
        val item = StatsSizeItem(this.itemView.context)
        item.text = text.toString()
        return item
    }

}



data class PeriodSummaryTO(
    val periodName: String,
    val timeSummary: String,
    val sizeMap: MutableMap<Int, Int>
)