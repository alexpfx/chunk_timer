package dev.alessi.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import dev.alessi.chunk.pomodoro.timer.android.R

class BadgedButton(context: Context?, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    private var txtBadge: TextView
    private var button: MaterialButton

    var isChecked
        get() = button.isChecked
        set(value) {
            button.isChecked = value
        }


    override fun setEnabled(enabled: Boolean) {
        button.isEnabled = enabled
    }

    override fun isEnabled(): Boolean {
        return button.isEnabled
    }

    override fun setOnClickListener(onClickListener: OnClickListener?) {
        button.setOnClickListener(onClickListener)
    }


    override fun setTag(tag: Any?) {
        button.tag = tag
    }


    override fun getTag(): Any {
        return button.tag
    }

    var badgeText
        get() = txtBadge.text.toString()
        set(value) {
            txtBadge.text = value
        }


    init {
        LayoutInflater.from(context).inflate(R.layout.compound_view_badged_button, this)
        txtBadge = findViewById(R.id.txtBadge)
        button = findViewById(R.id.btn)
        tag = super.getTag()
        val a = context?.obtainStyledAttributes(attrs, R.styleable.BadgedButton, 0, 0)

        val badgeText = a?.getString(R.styleable.BadgedButton_badgeText)
        val buttonLabel = a?.getString(R.styleable.BadgedButton_buttonLabel)
        val isChecked = a?.getBoolean(R.styleable.BadgedButton_isChecked, false)!!

        a.recycle()

        txtBadge.text = badgeText
        button.text = buttonLabel

        button.isChecked = isChecked
    }


}