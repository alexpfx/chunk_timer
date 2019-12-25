package dev.alessi.chunk.pomodoro.timer.android.components

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.material.animation.DrawableAlphaProperty
import dev.alessi.chunk.pomodoro.timer.android.R


const val proportion = 0.035

class BadgedButton(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private var checked = false
    private var txtBadge: TextView
    private var button: ImageButton
    private var checkedResource: Int = R.color.color_action_dividers
    private var uncheckedResource: Int = android.R.color.transparent


    fun setImageResource(drawable: Drawable){
        button.setImageDrawable(drawable)
    }


    override fun setOnClickListener(l: OnClickListener?) {

        button.setOnClickListener {
            l?.onClick(this)
        }

        super.setOnClickListener(l)
    }


    override fun setEnabled(enabled: Boolean) {
        button.isEnabled = enabled
        super.setEnabled(enabled)
    }

    var isChecked
        get() = checked
        set(value) {
            checked = value

            updateColor()

        }

    private fun updateColor() {
        if (checked) {

            setBackgroundResource(checkedResource)
        } else {
            setBackgroundResource(uncheckedResource)
        }
    }


    var text
        get() = txtBadge.text.toString()
        set(value) {
            txtBadge.text = value
        }


    init {

        LayoutInflater.from(context).inflate(R.layout.compound_view_badged_button, this)

        txtBadge = findViewById(R.id.txtBadge)
        button = findViewById(R.id.btnSize)

        tag = super.getTag()
        val a = context.obtainStyledAttributes(attrs, R.styleable.BadgedButton, 0, 0)


        txtBadge.text = a.getString(R.styleable.BadgedButton_labelText)

        checked = a.getBoolean(R.styleable.BadgedButton_isChecked, false)
        val buttonIcon = a.getResourceId(R.styleable.BadgedButton_imageResource, 0)

        val btnHeight = a.getDimension(R.styleable.BadgedButton_buttonWidth, 56.0f)
        val btnWidth = a.getDimension(R.styleable.BadgedButton_buttonWidth, 56.0f)

        applyDimensions(button, btnHeight.toDouble(), btnWidth.toDouble())

        val viewStrokeH = btnHeight + (btnHeight * proportion)
        val viewStrokeW = btnWidth + (btnWidth * proportion)

        button.setOnTouchListener { v, e ->
            onTouchEvent(e)

        }

        checkedResource =
            a.getResourceId(R.styleable.BadgedButton_backgroundChecked, checkedResource)
        uncheckedResource =
            a.getResourceId(R.styleable.BadgedButton_backgroundUnchecked, uncheckedResource)

        isClickable = true
        isFocusable = true
        button.setImageResource(buttonIcon)


        button.contentDescription = this.contentDescription

        a.recycle()

    }

    private fun applyDimensions(view: View, height: Double, width: Double) {
        val wDip = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            width.toFloat(),
            context.resources.displayMetrics
        ).toInt()


        val hDip = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            height.toFloat(),
            context.resources.displayMetrics
        ).toInt()

        view.layoutParams.height = hDip
        view.layoutParams.width = wDip

    }


}