package dev.alessi.chunk.pomodoro.timer.android.components

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import dev.alessi.chunk.pomodoro.timer.android.R


class StatsSizeItem (context: Context): LinearLayout(context, null){
    var textView: TextView
    var button: ImageButton


    var text
        get() = textView.text.toString()
        set(value) {
            textView.text = value
        }

    var imageResource: Int = 0
        set(value) {
            button.setImageResource(value)             
        }


    //    val buttonIcon = a.getResourceId(R.styleable.BadgedButton_imageResource, 0)
    init {
        LayoutInflater.from(context).inflate(R.layout.compound_view_stats_size_item, this)

        button = findViewById(R.id.button)
        textView = findViewById(R.id.textView)



    }

}