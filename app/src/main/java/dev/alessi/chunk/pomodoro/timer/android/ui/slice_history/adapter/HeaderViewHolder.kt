package dev.alessi.chunk.pomodoro.timer.android.ui.slice_history.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.alessi.chunk.pomodoro.timer.android.R

class HeaderViewHolder(
    val view: View,
    private val periods: List<PeriodSelectVo>,
    private val onClickListener: OnClickListener
) : RecyclerView.ViewHolder(view),
    ViewHolderBinder<DataItem.HeaderDataItem>,
    AdapterView.OnItemClickListener {

    private val txtTimeSummary: TextView? = view.findViewById(R.id.txt_time_summary)
    private val spinnerPeriod: AutoCompleteTextView? = view.findViewById(R.id.dropdown_menu)

    init {
        spinnerPeriod?.setText(periods[0].name)

        ArrayAdapter<PeriodSelectVo>(view.context, R.layout.item_dropdown, periods).also {
            spinnerPeriod?.setAdapter(it)
        }

        spinnerPeriod?.onItemClickListener = this
        txtTimeSummary?.text = "00:00"
    }


    companion object {
        fun from(
            viewGroup: ViewGroup,
            periods: List<PeriodSelectVo>,
            onClickListener: OnClickListener
        ): HeaderViewHolder {
            return HeaderViewHolder(
                LayoutInflater.from(
                    viewGroup.context
                ).inflate(R.layout.item_slice_history_header, viewGroup, false),
                periods, onClickListener
            )
        }


        interface OnClickListener {
            fun onClick(position: IntervalFilter)
        }
    }


    override fun bind(item: DataItem.HeaderDataItem) {
        txtTimeSummary?.text = item.summary
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val filter = periods[position].filter

        onClickListener.onClick(filter)

    }
}