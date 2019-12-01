package dev.alessi.chunk.pomodoro.timer.android.ui.task_stats

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.util.RuntimeViewFactory
import dev.alessi.chunk.pomodoro.timer.android.util.toDip


class TaskStatsRecyclerAdapter(private val itemList: List<PeriodSummaryTO>) :
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

    private var layoutSlicesSummary: FlexboxLayout = view.findViewById(R.id.layoutSlicesSummary)
    private var txtTimeSummary = view.findViewById<TextView>(R.id.txtTimeSummary)
    private var txtPeriod = view.findViewById<TextView>(R.id.txtPeriod)
    private var txtLabelMessageNotItems = view.findViewById<TextView>(R.id.txtLabelMessageNoItems)
    private var viewDivider = view.findViewById<View>(R.id.view_internal_divider2)
    private var context = itemView.context

    private val sliceIconSizeWidth = 24.toDip(context)
    private val sliceIconSizeHeight = sliceIconSizeWidth
    private val sliceIconColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)

    companion object {

        fun drawableRes(key: Int) = when (key) {
            0 -> R.drawable.sliced_1
            1 -> R.drawable.sliced_3
            2 -> R.drawable.sliced_5
            3 -> R.drawable.sliced_7
            4 -> R.drawable.sliced_full
            else -> R.drawable.sliced_full
        }

    }

    fun bind(periodSummaryTO: PeriodSummaryTO) {
        val periodName = context.getString(periodSummaryTO.period.labelKey)

        txtPeriod.text = periodName

        if (periodSummaryTO.isEmpty()) {
            setVisibiltyToEmptyPeriod()
        } else {
            setVisibilityToNotEmptyPeriod()

            txtTimeSummary.text = periodSummaryTO.toFormatedTime()

            periodSummaryTO.sizeMap.forEach {
                if (it.value > 0) {
                    layoutSlicesSummary.addView(
                        RuntimeViewFactory.createTextViewSliceSummary(
                            context,
                            it.value,
                            setupAndGetSliceDrawable(it.key)
                        )
                    )
                }
            }
        }
    }

    private fun setVisibilityToNotEmptyPeriod() {
        txtTimeSummary.visibility = View.VISIBLE
        layoutSlicesSummary.visibility = View.VISIBLE
    }

    private fun setVisibiltyToEmptyPeriod() {
        txtTimeSummary.visibility = View.GONE
        viewDivider.visibility = View.INVISIBLE
        txtLabelMessageNotItems.visibility = View.VISIBLE
        layoutSlicesSummary.visibility = View.INVISIBLE
    }

    private fun setupAndGetSliceDrawable(key: Int): Drawable {
        val drawable =
            ContextCompat.getDrawable(context, drawableRes(key))?.constantState?.newDrawable()
                ?.mutate()


        return drawable!!
    }

}
