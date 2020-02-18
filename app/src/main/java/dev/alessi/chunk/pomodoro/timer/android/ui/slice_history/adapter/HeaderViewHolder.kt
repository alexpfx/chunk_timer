package dev.alessi.chunk.pomodoro.timer.android.ui.slice_history.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.databinding.ItemSliceHistoryHeaderBinding

class HeaderViewHolder(
    private val binding: ItemSliceHistoryHeaderBinding,
    private val periods: List<PeriodSelectVo>,
    private val onClickListener: OnClickListener
) : RecyclerView.ViewHolder(binding.root),
    ViewHolderBinder<DataItem.HeaderDataItem>,
    AdapterView.OnItemClickListener {


    init {
        binding.dropdownMenu.apply {
            setText(periods[0].name)
            ArrayAdapter<PeriodSelectVo>(binding.root.context, R.layout.item_dropdown, periods).also {
                setAdapter(it)
            }
            onItemClickListener = this@HeaderViewHolder
        }

        binding.txtTimeSummary.text = "00:00"
    }


    companion object {
        fun from(
            viewGroup: ViewGroup,
            periods: List<PeriodSelectVo>,
            onClickListener: OnClickListener
        ): HeaderViewHolder {

            return HeaderViewHolder(
                ItemSliceHistoryHeaderBinding.inflate(
                    LayoutInflater.from(
                        viewGroup.context
                    ), viewGroup, false
                ),
                periods, onClickListener
            )
        }


        interface OnClickListener {
            fun onClick(position: IntervalFilter)
        }
    }


    override fun bind(item: DataItem.HeaderDataItem) {
        binding.txtTimeSummary.text = item.summary
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val filter = periods[position].filter

        onClickListener.onClick(filter)

    }
}