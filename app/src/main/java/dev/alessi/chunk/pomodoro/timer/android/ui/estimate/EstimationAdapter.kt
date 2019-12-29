package dev.alessi.chunk.pomodoro.timer.android.ui.estimate

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView
import dev.alessi.chunk.pomodoro.timer.android.ClockView
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.database.SizeTimeCountTO
import dev.alessi.chunk.pomodoro.timer.android.domain.SizeValue
import dev.alessi.chunk.pomodoro.timer.android.util.ViewUtils
import dev.alessi.chunk.pomodoro.timer.android.util.toFormatedSummary
import dev.alessi.chunk.pomodoro.timer.android.util.toFormatedTime

class EstimationAdapter(private val listeners: EstimationActionListeners) :

    RecyclerView.Adapter<ViewHolder>(), EstimationActionListeners {


    private var itemsSummarized = mutableListOf<SizeTimeCountTO>()


    private var countMaxDigits = 1
    private var minutesMaxDigits = 1
    private var editMode = false
    private var shouldAnimate = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_estimation_summary, parent, false)
        return ViewHolder(v, this)
    }

    override fun getItemCount(): Int {
        return itemsSummarized.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemsSummarized[position]

        holder.bind(
            item,
            countPad = countMaxDigits,
            minutesPad = minutesMaxDigits,
            editMode = editMode,
            animate = shouldAnimate
        )

    }

    fun setItems(
        list:
        List<SizeTimeCountTO>
    ) {

        this.itemsSummarized =
            list.onEach {
                val countDigits = it.count.toString().length
                val minutesDigits = it.timeMinutes.toString().length
                if (countDigits > countMaxDigits) {
                    countMaxDigits = countDigits
                }
                if (minutesDigits > minutesMaxDigits) {
                    minutesMaxDigits = minutesDigits
                }

            }.sortedBy { SizeValue(it.sizeId, it.timeMinutes) }.toMutableList()


        notifyDataSetChanged()
    }

    fun toggleEditMode(): Boolean {
        editMode = !editMode

        notifyDataSetChanged()

        return editMode
    }

    private val handler = Handler()

    fun setItem(entry: SizeTimeCountTO) {
        val index =
            itemsSummarized.indexOfFirst { it.sizeId == entry?.sizeId && it.timeMinutes == entry.timeMinutes }

        println("setItem $index $entry")


        if (index != -1 && entry != null) {
            itemsSummarized[index] = entry
            shouldAnimate = true
            notifyItemChanged(index)
            handler.postDelayed({
                shouldAnimate = false
            }, 10)
        } else {
            itemsSummarized.add(entry)
            setItems(itemsSummarized)
        }


    }

    override val onBtnPlus = View.OnClickListener {


        listeners.onBtnPlus.onClick(it)

    }
    override val onBtnMinus = View.OnClickListener {


        listeners.onBtnMinus.onClick(it)

    }
    override val onBtnDelete = View.OnClickListener {

        listeners.onBtnDelete.onClick(it)
    }


}


class ViewHolder(
    itemView: View,
    private val estimationActionListeners: EstimationActionListeners
) :
    RecyclerView.ViewHolder(itemView), EstimationActionListeners {
    private val clockView = itemView.findViewById<ClockView>(R.id.clockView)
    private val txtTotalTime = itemView.findViewById<TextView>(R.id.txtTotalTime)
    private val txtEstimationSummary = itemView.findViewById<TextView>(R.id.txtEstimationSummary)
    private val btnMinus: ImageButton = itemView.findViewById(R.id.btnMinus)
    private val btnPlus: ImageButton = itemView.findViewById(R.id.btnPlus)

    private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

    private val groupEditModel = itemView.findViewById<Group>(R.id.group_edit_mode)
    val context = itemView.context!!


    fun bind(
        item: SizeTimeCountTO,
        countPad: Int = 2,
        minutesPad: Int = 2,
        editMode: Boolean, animate: Boolean = false
    ) {
        clockView.minutes = item.timeMinutes

        updateSizes(item, countPad, minutesPad)

        groupEditModel.visibility = if (editMode) View.VISIBLE else View.GONE

        btnMinus.tag = item
        btnPlus.tag = item
        btnDelete.tag = item

        btnDelete.setOnClickListener(onBtnDelete)
        btnPlus.setOnClickListener(onBtnPlus)
        btnMinus.setOnClickListener(onBtnMinus)


        if (animate) {
            val fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
            val fadeOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)
            itemView.startAnimation(fadeIn)
            itemView.startAnimation(fadeOut)
        }


    }

    private fun updateSizes(
        item: SizeTimeCountTO,
        countPad: Int,
        minutesPad: Int
    ) {
        println("updateSizes $item")
        txtTotalTime.text = (item.count * item.timeMinutes).toFormatedTime()
        txtEstimationSummary.text = item.toFormatedSummary(item.count, countPad, minutesPad)
    }


    override val onBtnPlus = View.OnClickListener {


        estimationActionListeners.onBtnPlus.onClick(it)
    }
    override val onBtnMinus = View.OnClickListener {

        estimationActionListeners.onBtnMinus.onClick(it)

    }
    override val onBtnDelete = View.OnClickListener {


        estimationActionListeners.onBtnDelete.onClick(it)
    }


}


interface EstimationActionListeners {
    val onBtnPlus: View.OnClickListener
    val onBtnMinus: View.OnClickListener
    val onBtnDelete: View.OnClickListener
}

