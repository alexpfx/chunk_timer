package dev.alessi.chunk.pomodoro.timer.android.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import dev.alessi.chunk.pomodoro.timer.android.R

class BadgedButton(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private var checked = false
    private var txtBadge: TextView
    private var button: ImageButton
    private var checkedResource: Int = R.color.bg_color_light
    private var uncheckedResource: Int = android.R.color.transparent


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
        button = findViewById(R.id.btnSizePP)

        tag = super.getTag()
        val a = context.obtainStyledAttributes(attrs, R.styleable.BadgedButton, 0, 0)


        txtBadge.text = a.getString(R.styleable.BadgedButton_labelText)

        checked = a.getBoolean(R.styleable.BadgedButton_isChecked, false)!!
        val buttonIcon = a.getResourceId(R.styleable.BadgedButton_imageResource, 0)

        checkedResource =
            a.getResourceId(R.styleable.BadgedButton_backgroundChecked, checkedResource)
        uncheckedResource =
            a.getResourceId(R.styleable.BadgedButton_backgroundUnchecked, uncheckedResource)

        isClickable = true
        isFocusable = true
        button.setImageResource(buttonIcon)

        a.recycle()

    }


}