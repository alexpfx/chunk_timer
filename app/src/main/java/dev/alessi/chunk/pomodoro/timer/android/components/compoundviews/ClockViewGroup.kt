package dev.alessi.chunk.pomodoro.timer.android.components.compoundviews

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.children
import androidx.core.view.forEachIndexed
import dev.alessi.chunk.pomodoro.timer.android.R
import dev.alessi.chunk.pomodoro.timer.android.components.customview.ClockView

class ClockViewGroup(context: Context, attrs: AttributeSet?, defStyle: Int) : LinearLayout(context, attrs, defStyle) {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private var clockIndex = 0

    var message: String = ""
    private var llContentView: LinearLayout?
    private var txtMessage: TextView
    private var btnViewMore: AppCompatImageButton

    var onClockViewSelected: (clockView: ClockView) -> Unit = {}

    var onViewMoreButtonClick: (view: View) -> Unit = {}


    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        llContentView?.isEnabled = enabled
        llContentView?.children?.filter { it is ClockView }?.forEach {
            it.isEnabled = enabled
        }
//        btnViewMore.isActivated = enabled
        /*btnViewMore.isClickable = enabled
        btnViewMore.isFocusable = enabled*/
        btnViewMore.isEnabled = enabled


    }

    fun getAlpha(resId: Int): Float {
        val typedValue = TypedValue()
        context.resources.getValue(R.dimen.alpha_emphasis_high, typedValue, true)
        return typedValue.float

    }

    fun updateSizes(minutes: List<Int>) {
        val size = llContentView?.children?.count() ?: 0
        if (minutes.size != size) {
            throw IllegalStateException("Minutes array size and children size is not the same: ${minutes.size} != $size ")
        }

        llContentView?.forEachIndexed { index, view -> (view as ClockView).minutes = minutes[index] }


    }

    fun syncSelection(selectedIndex: Int) {
        llContentView?.children?.forEach {
            if (it is ClockView){
                it.isChecked = it.tag == selectedIndex
            }
        }
    }


    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        if (llContentView == null) {
            super.addView(child, params)
        } else {
            child?.apply {
                if (this is ClockView) {
                    child.tag = clockIndex++

                    child.setOnClickListener(OnClickListener { it ->
                        val clockView = it as ClockView
                        onClockViewSelected(clockView)
                        llContentView?.children?.filter { it is ClockView }?.forEach {
                            (it as ClockView).isChecked = false
                        }
                        clockView.isChecked = true
                    })
                    llContentView?.addView(child, params)
                } else {
                    throw IllegalStateException("Cannot add view")
                }

            }


        }
    }


    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.ClockViewGroup, -1, defStyle).apply {
            message = getString(R.styleable.ClockViewGroup_message) ?: ""
            recycle()
        }

        LayoutInflater.from(context).inflate(R.layout.compound_clock_view_selection_layout, this, true).apply {
            txtMessage = findViewById(R.id.txt_message)
            btnViewMore = findViewById(R.id.btn_view_more)

            btnViewMore.setOnClickListener {
                if (isEnabled) {
                    onViewMoreButtonClick(it)
                }
            }

            llContentView = findViewById(R.id.linear_layout_content_view)
            txtMessage.text = message
        }

    }


}